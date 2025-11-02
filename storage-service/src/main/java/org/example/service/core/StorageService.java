package org.example.service.core;

import org.example.entity.Storage;

import java.util.List;

public interface StorageService {
    Integer createStorage(Storage storage);

    Iterable<Storage> getAllStorages();

    List<Integer> deleteAll(List<Integer> ids);
}
