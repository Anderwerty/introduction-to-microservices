package org.example.service.core;

import org.example.entity.Storage;
import org.example.entity.StorageType;
import org.example.repository.StorageRepository;
import org.example.service.core.impl.StorageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

    @Mock
    private StorageRepository storageRepository;

    @InjectMocks
    private StorageServiceImpl storageService;

    @Test
    void createStorage_ShouldReturnGeneratedId() {
        Storage storage = new Storage();
        storage.setBucket("test-bucket");
        storage.setPath("/test/path");
        storage.setStorageType(StorageType.PERMANENT);

        Storage savedStorage = new Storage();
        savedStorage.setId(10);
        savedStorage.setBucket(storage.getBucket());
        savedStorage.setPath(storage.getPath());
        savedStorage.setStorageType(storage.getStorageType());

        when(storageRepository.save(storage)).thenReturn(savedStorage);

        Integer result = storageService.createStorage(storage);

        assertThat(result, is(10));
        verify(storageRepository).save(storage);
    }

    @Test
    void getAllStorages_ShouldReturnIterable() {
        List<Storage> storages = List.of(
                new Storage(1, StorageType.PERMANENT, "bucket1", "/path1"),
                new Storage(2, StorageType.STAGING, "bucket2", "/path2")
        );

        when(storageRepository.findAll()).thenReturn(storages);

        Iterable<Storage> actualStorages = storageService.getAllStorages();
        List<Storage> result = StreamSupport.stream(actualStorages.spliterator(), false)
                .toList();

        assertThat(result, hasSize(2));
        verify(storageRepository).findAll();
    }

    @Test
    void deleteAll_ShouldDeleteAndReturnExistingIds() {
        List<Integer> idsToDelete = List.of(1, 2, 3);
        List<Storage> existingStorages = List.of(
                new Storage(1, StorageType.STAGING, "bucket1", "/path1"),
                new Storage(2, StorageType.PERMANENT, "bucket2", "/path2")
        );

        when(storageRepository.findAllById(idsToDelete)).thenReturn(existingStorages);

        List<Integer> deletedIds = storageService.deleteAll(idsToDelete);

        assertThat(deletedIds, containsInAnyOrder(1, 2));
        verify(storageRepository).findAllById(idsToDelete);
        verify(storageRepository).deleteAllById(deletedIds);
    }
}