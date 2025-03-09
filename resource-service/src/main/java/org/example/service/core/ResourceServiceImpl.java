package org.example.service.core;

import lombok.AllArgsConstructor;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
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

    @Override
    public Integer storeFile(byte[] data) {

        Resource savedResource = resourceRepository.save(new Resource(data));

        return savedResource.getId();
    }

    @Override
    public byte[] getAudioData(Integer id) {
        return resourceRepository.findById(id).map(Resource::getFile)
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
