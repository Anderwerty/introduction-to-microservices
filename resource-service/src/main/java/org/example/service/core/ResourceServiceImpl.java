package org.example.service.core;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.exception.IllegalResourceException;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceServiceImpl implements ResourceService {
    private static final String[] ALLOWED_CONTENT_TYPES = {"audio/mpeg"};

    private final ResourceRepository resourceRepository;

    @Override
    public Integer storeFile(MultipartFile file) {

        validateFile(file);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Resource savedResource = resourceRepository.save(new Resource(bytes));


        return savedResource.getId();
    }

    @Override
    public byte[] getAudioData(Integer id) {
        return resourceRepository.findById(id).map(Resource::getFile)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Resources with id=[%d] doesn't exist", id)));
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

    private static void validateFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalResourceException("File doesn't exist");
        }
        if (file.isEmpty()) {
            throw new IllegalResourceException("File is empty");
        }
        boolean isNotSupported = Arrays.stream(ALLOWED_CONTENT_TYPES).noneMatch(x -> x.equals(file.getContentType()));
        if (isNotSupported) {
            throw new IllegalResourceException("Not valid content type");
        }
    }
}
