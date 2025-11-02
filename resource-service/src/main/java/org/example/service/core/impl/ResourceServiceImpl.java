package org.example.service.core.impl;

import org.example.entity.FileState;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.client.MessagePublisher;
import org.example.service.client.StorageClient;
import org.example.service.core.KeyGenerator;
import org.example.service.core.ResourceService;
import org.example.service.core.S3Service;
import org.example.service.dto.FileUrl;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.example.service.exception.ResourceNotFoundException;
import org.example.service.mapper.FileUrlMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    private final KeyGenerator keyGenerator;

    private final S3Service s3Service;

    private final MessagePublisher<ResourceEvent> messagePublisher;

    private final StorageClient storageClient;

    private final FileUrlMapper fileUrlMapper;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               KeyGenerator keyGenerator,
                               S3Service s3Service,
                               @Qualifier("music.event.message.publisher")
                               MessagePublisher<ResourceEvent> messagePublisher,
                               @Qualifier("storage.rest.client")
                               StorageClient storageClient,
                               FileUrlMapper fileUrlMapper) {
        this.resourceRepository = resourceRepository;
        this.keyGenerator = keyGenerator;
        this.s3Service = s3Service;
        this.messagePublisher = messagePublisher;
        this.storageClient = storageClient;
        this.fileUrlMapper = fileUrlMapper;
    }

    @Override
    public Integer storeFile(byte[] data) {

        List<StorageDetailsResponse> storages = storageClient.getStorageDetailsResponses();

        StorageDetailsResponse stagingStorage =
                storages.stream()
                        .filter(s -> s.getStorageType() == StorageType.STAGING)
                        .findAny()
                        .orElseThrow(ResourceNotFoundException::new);

        String generatedKey = keyGenerator.generateKey(stagingStorage.getPath());
        FileUrl fileUrlToSave = FileUrl.builder()
                .bucketName(stagingStorage.getBucket())
                .key(generatedKey)
                .build();
        FileUrl fileUrl = s3Service.uploadFile(fileUrlToSave, data);

        org.example.entity.FileUrl fileUrlEntity = fileUrlMapper.mapToEntity(fileUrl);

        Resource entity = new Resource(fileUrlEntity);
        entity.setFileState(FileState.STAGING);
        Resource savedResource = resourceRepository.save(entity);

        ResourceEvent resourceEvent = new ResourceEvent(savedResource.getId());
        messagePublisher.publishMessage(resourceEvent);

        return savedResource.getId();
    }

    @Override
    public byte[] getAudioData(Integer id) {
        return resourceRepository.findById(id)
                .map(Resource::getFileUrl)
                .map(fileUrlMapper::mapToDto)
                .map(s3Service::downloadFile)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Resource with ID=%d not found", id)));
    }

    @Transactional
    @Override
    public List<Integer> deleteAll(List<Integer> ids) {
        Iterable<Resource> existedResources = resourceRepository.findAllById(ids);
        List<Integer> existedIds = StreamSupport.stream(existedResources.spliterator(), false)
                .map(Resource::getId)
                .toList();

        if (existedIds.isEmpty()) {
            return existedIds;
        }
        resourceRepository.deleteAllById(existedIds);

        List<FileUrl> urlsToDelete = StreamSupport.stream(existedResources.spliterator(), false)
                .map(Resource::getFileUrl)
                .map(fileUrlMapper::mapToDto)
                .toList();

        s3Service.deleteAll(urlsToDelete);

        return existedIds;
    }

}
