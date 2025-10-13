package org.example.service.core;

import org.example.repository.ResourceRepository;
import org.example.service.core.impl.ResourceServiceImpl;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
    KeyGenerator keyGenerator;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void storeFileShouldSave() {
        when(keyGenerator.generateKey()).thenReturn(GENERATED_KEY1);
        when(s3Service.uploadFile(GENERATED_KEY1, FILE_BYTES)).thenReturn(DUMMY_URL1);
        when(resourceRepository.save(RESOURCE_WITHOUT_ID)).thenReturn(RESOURCE_WITH_ID);

        Integer actualId = resourceService.storeFile(FILE_BYTES);
        Integer expectedId = 1;

        assertEquals(expectedId, actualId);
    }

    @Test
    void getAudioDataShouldReturnFile() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(RESOURCE_WITH_ID));
        when(s3Service.downloadFile("dummy-bucket/" + GENERATED_KEY1)).thenReturn(FILE_BYTES);

        byte[] actual = resourceService.getAudioData(1);
        assertEquals(FILE_BYTES, actual);
    }

    @Test
    void getAudioDataShouldThrowExceptionIfNotExist() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> resourceService.getAudioData(1));
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
        verify(s3Service).deleteAll(Arrays.asList(DUMMY_URL1, DUMMY_URL2));
    }

    @Test
    void deleteShouldNotRemoveItemsFromDBIfNotSuch() {
        List<Integer> ids = List.of(1, 2, 3);
        when(resourceRepository.findAllById(ids)).thenReturn(List.of());

        List<Integer> actualIds = resourceService.deleteAll(ids);
        List<Integer> existedIds = List.of();
        assertEquals(actualIds, existedIds);
        verify(resourceRepository, never()).deleteAllById(existedIds);
        verify(s3Service, never()).deleteAll(Arrays.asList(DUMMY_URL1, DUMMY_URL2));
    }
}