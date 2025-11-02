package org.example.service.core.impl;

import lombok.AllArgsConstructor;
import org.example.entity.Storage;
import org.example.repository.StorageRepository;
import org.example.service.core.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    @Override
    public Integer createStorage(Storage storage) {
        return storageRepository.save(storage).getId();
    }

    @Override
    public Iterable<Storage> getAllStorages() {
        return storageRepository.findAll();
    }

    @Override
    public List<Integer> deleteAll(List<Integer> ids) {
        Iterable<Storage> existedListStorages = storageRepository.findAllById(ids);

        List<Integer> existedIds = StreamSupport.stream(existedListStorages.spliterator(), false)
                .map(Storage::getId)
                .toList();
        storageRepository.deleteAllById(existedIds);

        return existedIds;
    }
}
