package org.example.service.rest;

import org.example.service.dto.StorageCreationRequest;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;

import java.util.List;

public interface StorageRestService {
    Identifiable<Integer> createStorage(StorageCreationRequest request);

    List<StorageDetailsResponse> getAllStorages();

    Identifiables<Integer> deleteStorages(String ids);
}
