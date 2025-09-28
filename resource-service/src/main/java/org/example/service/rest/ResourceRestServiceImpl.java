package org.example.service.rest;

import org.example.service.client.MessagePublisher;
import org.example.service.core.MetadataExtracter;
import org.example.service.core.ResourceService;
import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;
import org.example.service.dto.ResourceEvent;
import org.example.service.exception.IllegalResourceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Transactional
@Service
public class ResourceRestServiceImpl implements ResourceRestService {

    private static final String[] ALLOWED_CONTENT_TYPES = {"audio/mpeg"};

    private final ResourceService resourceService;

    private final MetadataExtracter metadataExtracter;

    private final MessagePublisher<ResourceEvent> messagePublisher;

    public ResourceRestServiceImpl(ResourceService resourceService, MetadataExtracter metadataExtracter,
                                   @Qualifier("music.event.message.publisher")
                                   MessagePublisher<ResourceEvent> messagePublisher) {
        this.resourceService = resourceService;
        this.metadataExtracter = metadataExtracter;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public Identifiable<Integer> storeFile(byte[] bytes) throws IllegalResourceException {

        validateFile(bytes);

        Integer id = resourceService.storeFile(bytes);
        ResourceEvent  resourceEvent = new ResourceEvent(id);
        messagePublisher.publishMessage(resourceEvent);
        return new Identifiable<>(id);
    }

    @Override
    public byte[] getAudioData(String id) {
        Integer identifier = Integer.valueOf(id);
        return resourceService.getAudioData(identifier);
    }

    @Override
    public Identifiables<Integer> deleteResources(String idsParameter) {
        if (idsParameter == null || idsParameter.isEmpty()) {
            return new Identifiables<>(Collections.emptyList());
        }

        List<Integer> ids = Arrays.stream(idsParameter.split(","))
                .map(Integer::valueOf)
                .toList();
        if (ids.isEmpty()) {
            return new Identifiables<>(Collections.emptyList());
        }

        List<Integer> deletedIds = resourceService.deleteAll(ids);

        return new Identifiables<>(deletedIds);
    }

    private void validateFile(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalResourceException("File doesn't exist");
        }
        if (bytes.length == 0) {
            throw new IllegalResourceException("File is empty");
        }
        String mimeType = metadataExtracter.getMimeType(bytes);
        boolean isNotSupported = Arrays.stream(ALLOWED_CONTENT_TYPES).noneMatch(x -> x.equals(mimeType));
        if (isNotSupported) {
            String message = String.format("Invalid file format: %s. Only MP3 files are allowed", mimeType);
            throw new IllegalResourceException(message);
        }
    }

}
