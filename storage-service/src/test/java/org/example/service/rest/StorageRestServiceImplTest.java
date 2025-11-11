package org.example.service.rest;

import org.example.entity.Storage;
import org.example.service.core.StorageService;
import org.example.service.dto.*;
import org.example.service.mapper.StorageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class StorageRestServiceImplTest {
    @Mock
    private StorageService storageService;

    @Mock
    private StorageMapper storageMapper;

    @InjectMocks
    private StorageRestServiceImpl storageRestService;

    @Test
    void createStorageShouldMapRequestAndReturnId() {
        StorageCreationRequest request = new StorageCreationRequest(StorageType.PERMANENT, "bucket-1", "/data");
        Storage storage = new Storage();
        storage.setStorageType(org.example.entity.StorageType.PERMANENT);
        storage.setBucket("bucket-1");
        storage.setPath("/data");

        when(storageMapper.mapToEntity(request)).thenReturn(storage);
        when(storageService.createStorage(storage)).thenReturn(10);

        Identifiable<Integer> result = storageRestService.createStorage(request);

        assertThat(result.getId(), is(10));
        verify(storageMapper).mapToEntity(request);
        verify(storageService).createStorage(storage);
    }

    @Test
    void getAllStoragesShouldMapEntitiesToResponses() {
        Storage s1 = new Storage(1, org.example.entity.StorageType.PERMANENT, "bucket1", "/path1");
        Storage s2 = new Storage(2, org.example.entity.StorageType.STAGING, "bucket2", "/path2");

        StorageDetailsResponse r1 = new StorageDetailsResponse(1, StorageType.PERMANENT, "bucket1", "/path1");
        StorageDetailsResponse r2 = new StorageDetailsResponse(2, StorageType.PERMANENT, "bucket2", "/path2");

        when(storageService.getAllStorages()).thenReturn(List.of(s1, s2));
        when(storageMapper.mapToDto(s1)).thenReturn(r1);
        when(storageMapper.mapToDto(s2)).thenReturn(r2);

        List<StorageDetailsResponse> result = storageRestService.getAllStorages();

        assertThat(result, hasSize(2));
        assertThat(result.stream().map(StorageDetailsResponse::getId).toList(), containsInAnyOrder(1, 2));
        verify(storageService).getAllStorages();
        verify(storageMapper, times(2)).mapToDto(any(Storage.class));
    }

    @Test
    void deleteStoragesShouldReturnDeletedIds() {
        String idsParam = "1,2,3";
        List<Integer> ids = List.of(1, 2, 3);
        List<Integer> deletedIds = List.of(1, 3);

        when(storageService.deleteAll(ids)).thenReturn(deletedIds);

        Identifiables<Integer> result = storageRestService.deleteStorages(idsParam);

        assertThat(result.getIds(), containsInAnyOrder(1, 3));
        verify(storageService).deleteAll(ids);
    }

    @Test
    void deleteStoragesShouldReturnEmptyWhenIdsParameterIsNullOrEmpty() {
        Identifiables<Integer> nullResult = storageRestService.deleteStorages(null);
        assertThat(nullResult.getIds(), hasSize(0));

        Identifiables<Integer> emptyResult = storageRestService.deleteStorages("");
        assertThat(emptyResult.getIds(), hasSize(0));

        verifyNoInteractions(storageService);
    }

}