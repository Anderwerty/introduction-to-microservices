package org.example.service.rest;

import lombok.AllArgsConstructor;
import org.example.entity.Storage;
import org.example.service.core.StorageService;
import org.example.service.dto.*;
import org.example.service.mapper.StorageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@Transactional
@Service
@AllArgsConstructor
public class StorageRestServiceImpl implements StorageRestService {

    private final StorageService storageService;
    private final StorageMapper storageMapper;

    @Override
    public Identifiable<Integer> createStorage(StorageCreationRequest request) {
        Storage storage = storageMapper.mapToEntity(request);
        return new Identifiable<>(storageService.createStorage(storage));
    }

    @Transactional(readOnly = true)
    @Override
    public List<StorageDetailsResponse> getAllStorages() {
        return StreamSupport.stream(storageService.getAllStorages().spliterator(), false)
                .map(storageMapper::mapToDto)
                .toList();
    }

    @Override
    public Identifiables<Integer> deleteStorages(String idsParameter) {
        if (idsParameter == null || idsParameter.isEmpty()) {
            return new Identifiables<>(Collections.emptyList());
        }

        List<Integer> ids = Arrays.stream(idsParameter.split(","))
                .map(Integer::valueOf)
                .toList();
        if (ids.isEmpty()) {
            return new Identifiables<>(Collections.emptyList());
        }

        List<Integer> deletedIds = storageService.deleteAll(ids);

        return new Identifiables<>(deletedIds);
    }

}
