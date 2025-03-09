package org.example.service.core;

import org.example.entity.SongMetadata;
import org.example.repository.MetadataRepository;
import org.example.service.exception.NotFoundException;
import org.example.service.exception.SongAlreadyExistRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.example.service.DataUtils.initSongMetaData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongMetadataServiceImplTest {

    @Mock
    private MetadataRepository metadataRepository;

    @InjectMocks
    private SongMetaDataServiceImpl songMetaDataService;

    @Test
    void storeMetaDataShouldSaveEntity() {
        SongMetadata songMetaData = initSongMetaData(null);
        SongMetadata songMetadataSaved = initSongMetaData(1);
        when(metadataRepository.save(songMetaData)).thenReturn(songMetadataSaved);

        Integer actualId = songMetaDataService.storeMetaData(songMetaData);
        Integer expectedId = 1;

        assertEquals(expectedId, actualId);
    }

    @Test
    void storeMetaDataShouldThrowExceptionWhenSongWithSuchIdExist() {
        SongMetadata songMetaData = initSongMetaData(1);
        when(metadataRepository.existsById(1)).thenReturn(true);

        SongAlreadyExistRuntimeException exception = assertThrows(SongAlreadyExistRuntimeException.class,
                () -> songMetaDataService.storeMetaData(songMetaData));
        assertEquals("Metadata for resource ID=1 already exists", exception.getMessage());

    }

    @Test
    void getMetaDataShouldReturnEntity() {
        SongMetadata songMetaData = initSongMetaData(1);
        when(metadataRepository.findById(1)).thenReturn(Optional.of(songMetaData));

        SongMetadata actual = songMetaDataService.getMetaData(1);
        assertEquals(songMetaData, actual);
    }

    @Test
    void getMetaDataShouldThrowExceptionIfNotExist() {
        when(metadataRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> songMetaDataService.getMetaData(1));
        assertEquals("Song metadata for ID=1 not found", exception.getMessage());
    }

    @Test
    void deleteShouldRemoveItemsFromDB() {
        List<Integer> ids = List.of(1, 2, 3);
        when(metadataRepository.findAllById(ids)).thenReturn(List.of(initSongMetaData(1), initSongMetaData(2)));

        List<Integer> actualIds = songMetaDataService.deleteAll(ids);
        List<Integer> existedIds = List.of(1, 2);
        assertEquals(existedIds, actualIds);
        verify(metadataRepository).deleteAllById(existedIds);
    }

}
