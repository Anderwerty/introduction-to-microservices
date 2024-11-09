package org.example.service.core;

import org.example.entity.SongMetaData;
import org.example.repository.MetadataRepository;
import org.example.service.exception.NotFoundException;
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
class SongMetaDataServiceImplTest {

    @Mock
    private MetadataRepository metadataRepository;

    @InjectMocks
    private SongMetaDataServiceImpl songMetaDataService;

    @Test
    void storeMetaDataShouldSaveEntity() {
        SongMetaData songMetaData = initSongMetaData(null);
        SongMetaData songMetaDataSaved = initSongMetaData(1);
        when(metadataRepository.save(songMetaData)).thenReturn(songMetaDataSaved);

        Integer actualId = songMetaDataService.storeMetaData(songMetaData);
        Integer expectedId = 1;

        assertEquals(actualId, expectedId);
    }

    @Test
    void getMetaDataShouldReturnEntity() {
        SongMetaData songMetaData = initSongMetaData(1);
        when(metadataRepository.findById(1)).thenReturn(Optional.of(songMetaData));

        SongMetaData actual = songMetaDataService.getMetaData(1);
        assertEquals(actual, songMetaData);
    }

    @Test
    void getMetaDataShouldThrowExceptionIfNotExist() {
        when(metadataRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> songMetaDataService.getMetaData(1));
        assertEquals(exception.getMessage(), "Song metadata with id=[1] doesn't exist");
    }

    @Test
    void deleteShouldRemoveItemsFromDB() {
        List<Integer> ids = List.of(1, 2, 3);
        when(metadataRepository.findAllById(ids)).thenReturn(List.of(initSongMetaData(1), initSongMetaData(2)));

        List<Integer> actualIds = songMetaDataService.deleteAll(ids);
        List<Integer> existedIds = List.of(1, 2);
        assertEquals(actualIds, existedIds);
        verify(metadataRepository).deleteAllById(existedIds);
    }

}
