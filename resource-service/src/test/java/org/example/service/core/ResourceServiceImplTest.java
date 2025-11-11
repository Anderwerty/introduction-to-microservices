package org.example.service.core;

import org.example.entity.FileState;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.client.MessagePublisher;
import org.example.service.client.StorageClient;
import org.example.service.core.impl.ResourceServiceImpl;
import org.example.service.dto.FileUrl;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.example.service.exception.ResourceNotFoundException;
import org.example.service.mapper.FileUrlMapper;
import org.example.service.mapper.FileUrlMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.example.DataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private KeyGenerator keyGenerator;

    @Mock
    private StorageClient storageClient;

    @Mock
    private MessagePublisher<ResourceEvent> messagePublisher;

    @Spy
    private FileUrlMapper fileUrlMapper = new FileUrlMapperImpl();

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void storeFileShouldSave() {
        StorageDetailsResponse storageResponse = new StorageDetailsResponse(1, StorageType.STAGING, BUCKET_NAME, PATH);
        StorageDetailsResponse permanentResponse = new StorageDetailsResponse(2, StorageType.PERMANENT, BUCKET_NAME, PATH);
        when(storageClient.getStorageDetailsResponses())
                .thenReturn(List.of(storageResponse, permanentResponse));
        String generatedKey = PATH + "/" + GENERATED_KEY1;
        when(keyGenerator.generateKey(PATH)).thenReturn(generatedKey);
        FileUrl fileUrlToSave = FileUrl.builder()
                .bucketName(storageResponse.getBucket())
                .key(generatedKey)
                .build();
        String fullUrl = "http://localhost:4566/" + BUCKET_NAME + "/" + PATH + "/" + GENERATED_KEY1;
        FileUrl savedFileUrl = FileUrl.builder()
                .bucketName(storageResponse.getBucket())
                .key(generatedKey)
                .fullUrl(fullUrl)
                .build();
        when(s3Service.uploadFile(fileUrlToSave, FILE_BYTES)).thenReturn(savedFileUrl);
        Resource resource = new Resource(new org.example.entity.FileUrl(fullUrl, BUCKET_NAME, generatedKey));
        resource.setFileState(FileState.STAGING);
        Resource savedResource = new Resource(1, new org.example.entity.FileUrl(fullUrl, BUCKET_NAME, generatedKey), FileState.STAGING);
        when(resourceRepository.save(resource)).thenReturn(savedResource);

        Integer actualId = resourceService.storeFile(FILE_BYTES);
        Integer expectedId = 1;

        assertEquals(expectedId, actualId);
    }

    @Test
    void getAudioDataShouldReturnFile() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(RESOURCE_WITH_ID));
        when(s3Service.downloadFile(initFileUrlDto(BUCKET_NAME, GENERATED_KEY1, DUMMY_URL1))).thenReturn(FILE_BYTES);

        byte[] actual = resourceService.getAudioData(1);
        assertEquals(FILE_BYTES, actual);
    }

    @Test
    void getAudioDataShouldThrowExceptionIfNotExist() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.getAudioData(1));
        assertEquals("Resource with ID=1 not found", exception.getMessage());
    }

    @Test
    void deleteShouldRemoveItemsFromDB() {
        List<Integer> ids = List.of(1, 2, 3);
        when(resourceRepository.findAllById(ids)).thenReturn(List.of(RESOURCE_WITH_ID, RESOURCE_WITH_ID_2));

        List<Integer> actualIds = resourceService.deleteAll(ids);
        List<Integer> existedIds = List.of(1, 2);
        assertEquals(actualIds, existedIds);
        verify(resourceRepository).deleteAllById(existedIds);
        verify(s3Service).deleteAll(List.of(initFileUrlDto(BUCKET_NAME, GENERATED_KEY1, DUMMY_URL1),
                initFileUrlDto(BUCKET_NAME, GENERATED_KEY2, DUMMY_URL2)));
    }

    @Test
    void deleteShouldNotRemoveItemsFromDBIfNotSuch() {
        List<Integer> ids = List.of(1, 2, 3);
        when(resourceRepository.findAllById(ids)).thenReturn(List.of());

        List<Integer> actualIds = resourceService.deleteAll(ids);
        List<Integer> existedIds = List.of();
        assertEquals(actualIds, existedIds);
        verify(resourceRepository, never()).deleteAllById(existedIds);
        verify(s3Service, never()).deleteAll(any());
    }
}