package org.example.service.rest;

import org.example.service.exception.IllegalResourceException;
import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;

public interface ResourceRestService {
    Identifiable<Integer> storeFile(byte[] bytes) throws IllegalResourceException;

    byte[] getAudioData(String id);

    Identifiables<Integer> deleteResources(String ids);
}
