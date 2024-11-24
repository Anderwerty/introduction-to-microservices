package org.example.service.core;

import org.example.repository.ResourceRepository;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.example.DataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void storeFileShouldSave() {
        when(resourceRepository.save(RESOURCE_WITHOUT_ID)).thenReturn(RESOURCE_WITH_ID);

        Integer actualId = resourceService.storeFile(FILE_BYTES);
        Integer expectedId = 1;

        assertEquals(actualId, expectedId);
    }

    @Test
    void getAudioDataShouldReturnFile() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(RESOURCE_WITH_ID));

        byte[] actual = resourceService.getAudioData(1);
        assertEquals(actual, FILE_BYTES);
    }

    @Test
    void getAudioDataShouldThrowExceptionIfNotExist() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> resourceService.getAudioData(1));
        assertEquals(exception.getMessage(), "Resources with id=[1] doesn't exist");
    }

    @Test
    void deleteShouldRemoveItemsFromDB() {
        List<Integer> ids = List.of(1, 2, 3);
        when(resourceRepository.findAllById(ids)).thenReturn(List.of(RESOURCE_WITH_ID, RESOURCE_WITH_ID_2));

        List<Integer> actualIds = resourceService.deleteAll(ids);
        List<Integer> existedIds = List.of(1, 2);
        assertEquals(actualIds, existedIds);
        verify(resourceRepository).deleteAllById(existedIds);
    }
}