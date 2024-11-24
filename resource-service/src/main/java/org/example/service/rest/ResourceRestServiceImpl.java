package org.example.service.rest;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.service.core.MetadataExtracter;
import org.example.service.core.ResourceService;
import org.example.service.exception.IllegalResourceException;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;
import org.springframework.beans.factory.annotation.Value;
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

    private final int idsParameterLengthLimit;

    public ResourceRestServiceImpl(ResourceService resourceService, MetadataExtracter metadataExtracter,
                                   @Value("${ids.parameter.length.limit}") int idsParameterLengthLimit) {
        this.resourceService = resourceService;
        this.metadataExtracter = metadataExtracter;
        this.idsParameterLengthLimit = idsParameterLengthLimit;
    }


    @Override
    public Identifiable<Integer> storeFile(byte[] bytes) throws IllegalResourceException {

        validateFile(bytes);

        return new Identifiable<>(resourceService.storeFile(bytes));
    }

    @Override
    public byte[] getAudioData(String id) {
        try {
            Integer identifier = Integer.valueOf(id);
            return resourceService.getAudioData(identifier);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Id [%s] is not int type", id), e);
        }
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

        return  new Identifiables(resourceService.deleteAll(ids));
    }

    private void validateIdsParameter(String idsParameter) {
        int length = idsParameter.length();
        if (length >= idsParameterLengthLimit) {
            throw new IllegalArgumentException(String.format("Too long ids parameter length [%d]", length));
        }
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
            throw new IllegalResourceException(String.format("Not valid content type [%s]", mimeType));
        }
    }

}
