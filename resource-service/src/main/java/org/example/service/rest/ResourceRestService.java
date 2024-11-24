package org.example.service.rest;

import org.example.service.exception.IllegalResourceException;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;

import java.util.List;

public interface ResourceRestService {
    Identifiable<Integer> storeFile(byte[] bytes) throws IllegalResourceException;

    byte[] getAudioData(String id);

    Identifiables<Integer> deleteResources(String ids);
}
