package org.example.service.core.impl;

import lombok.AllArgsConstructor;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.core.KeyGenerator;
import org.example.service.core.ResourceService;
import org.example.service.core.S3Service;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    private KeyGenerator keyGenerator;

    private final S3Service s3Service;

    @Override
    public Integer storeFile(byte[] data) {

        String generatedKey = keyGenerator.generateKey();
        String fileUrl = s3Service.uploadFile(generatedKey, data);

        Resource savedResource = resourceRepository.save(new Resource(fileUrl));

        return savedResource.getId();
    }

    @Override
    public byte[] getAudioData(Integer id) {
        return resourceRepository.findById(id)
                .map(Resource::getFileUrl)
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
        resourceRepository.deleteAllById(existedIds);

        return existedIds;
    }

}
