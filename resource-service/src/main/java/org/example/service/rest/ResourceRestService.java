package org.example.service.rest;

import org.example.service.exception.IllegalResourceException;
import org.example.service.rest.dto.Identifiable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceRestService {
    Identifiable<Integer> storeFile(byte[] bytes) throws IllegalResourceException;

    byte[] getAudioData(String id);

    List<Integer> deleteResources(String ids);
}
