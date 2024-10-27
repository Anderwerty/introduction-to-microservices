package org.example.service.rest;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.controller.dto.Identifiable;
import org.example.service.core.ResourceService;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceRestServiceImpl implements ResourceRestService {

    private final ResourceService resourceService;


    @Override
    public Identifiable<Integer> storeFile(MultipartFile file) {
        return new Identifiable<>(resourceService.storeFile(file));
    }

    @Override
    public byte[] getAudioData(String id) {
        try {
            Integer identifier = Integer.valueOf(id);
            return resourceService.getAudioData(identifier);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(String.format("Id [%s] is not int type", id), e);
        }
    }

    @Override
    public List<Integer> deleteResources(String idsParameter) {
        if(idsParameter == null || idsParameter.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = Arrays.stream(idsParameter.split(",")).map(String::trim)
                .filter(NumberUtils::isCreatable)
                .map(Integer::valueOf)
                .toList();
        if(ids.isEmpty()){
            return Collections.emptyList();
        }

        return resourceService.deleteAll(ids);
    }

}
