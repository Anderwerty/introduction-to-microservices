package org.example.service.client;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.entity.FileState;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.core.S3Service;
import org.example.service.dto.FileUrl;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class ResourcePermanentEventHandlerImpl implements ResourceEventHandler {

    private final ResourceRepository resourceRepository;
    private final StorageClient storageClient;
    private final S3Service s3Service;

    @RabbitListener(queues = "${message.resource.queue.saved.name:}")
    @Override
    @Transactional
    public void handle(ResourceEvent event) {
        log.debug(" Received event: {}", event);


        Resource resource = resourceRepository.findById(event.getResourceId())
                .orElseThrow(ResourceNotFoundException::new);

        System.out.println("***** old resource:" +resource);


        resource.setFileState(FileState.PERMANENT);

        List<StorageDetailsResponse> storages = storageClient.getStorageDetailsResponses();

        StorageDetailsResponse stagingStorage =
                storages.stream()
                        .filter(s -> s.getStorageType() == StorageType.STAGING)
                        .findAny()
                        .orElseThrow(ResourceNotFoundException::new);

        StorageDetailsResponse permanentStorage =
                storages.stream()
                        .filter(s -> s.getStorageType() == StorageType.PERMANENT)
                        .findAny()
                        .orElseThrow(ResourceNotFoundException::new);

        String originalKey = resource.getFileUrl().getKey();
        String originalBucket = resource.getFileUrl().getBucketName();

        FileUrl originalFileUrl = FileUrl.builder()
                .bucketName(originalBucket)
                .key(originalKey)
                .build();
        byte[] bytes = s3Service.downloadFile(originalFileUrl);

        String key = permanentStorage.getPath() + originalKey.replace(stagingStorage.getPath(), "");

        FileUrl fileUrlToSave = FileUrl.builder()
                .bucketName(permanentStorage.getBucket())
                .key(key)
                .build();

        FileUrl savedFileUrl = s3Service.uploadFile(fileUrlToSave, bytes);
        s3Service.deleteAll(List.of(originalFileUrl));

        resource.getFileUrl().setBucketName(savedFileUrl.getBucketName());
        resource.getFileUrl().setKey(savedFileUrl.getKey());
        resource.getFileUrl().setFullUrl(savedFileUrl.getFullUrl());

        System.out.println("***** new resource:" +resource);

        resourceRepository.save(resource);
    }

}
