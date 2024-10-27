package org.example.service.rest;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.service.exception.IllegalResourceException;
import org.example.service.rest.dto.Identifiable;
import org.example.service.core.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceRestServiceImpl implements ResourceRestService {

    private static final int IDS_PARAMETER_LENGTH_LIMIT = 200;
    private static final String[] ALLOWED_CONTENT_TYPES = {"audio/mpeg"};

    private final ResourceService resourceService;


    @Override
    public Identifiable<Integer> storeFile(MultipartFile file) {

        validateFile(file);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    public List<Integer> deleteResources(String idsParameter) {
        if (idsParameter == null || idsParameter.isEmpty()) {
            return Collections.emptyList();
        }

        validateIdsParameter(idsParameter);

        List<Integer> ids = Arrays.stream(idsParameter.split(",")).map(String::trim)
                .filter(NumberUtils::isCreatable)
                .map(Integer::valueOf)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return resourceService.deleteAll(ids);
    }

    private void validateIdsParameter(String idsParameter) {
        int length = idsParameter.length();
        if (length >= IDS_PARAMETER_LENGTH_LIMIT) {
            throw new IllegalArgumentException(String.format("Too long ids parameter length [%d]", length));
        }
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
