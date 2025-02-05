package org.example.service.rest;

import org.example.entity.SongMetadata;
import org.example.service.core.SongMetaDataService;
import org.example.service.mapper.SongMetaDataMapper;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.example.service.DataUtils.initSongMetaData;
import static org.example.service.DataUtils.initSongMetaDataDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongMetadataRestServiceImplTest {
    @Mock
    private SongMetaDataService songMetaDataService;

    @Mock
    private SongMetaDataMapper songMetaDataMapper;

    @InjectMocks
    private SongMetaDataRestServiceImpl songMetaDataRestService;

    @Test
    void storeMetaDataShouldStoreData() {
        SongMetadata entity = initSongMetaData(1);
        SongMetaDataDto dto = initSongMetaDataDto(1);
        when(songMetaDataMapper.mapToEntity(dto)).thenReturn(entity);
        when(songMetaDataService.storeMetaData(entity)).thenReturn(1);

        Identifiable<Integer> identifiable = songMetaDataRestService.storeMetaData(dto);
        assertEquals(new Identifiable<>(1), identifiable);
    }

    @Test
    void getMetaDataShouldReturnSongMetaData() {
        SongMetadata entity = initSongMetaData(1);
        SongMetaDataDto dto = initSongMetaDataDto(1);
        when(songMetaDataService.getMetaData(1)).thenReturn(entity);
        when(songMetaDataMapper.mapToDto(entity)).thenReturn(dto);

        SongMetaDataDto actual = songMetaDataRestService.getMetaData("1");
        assertEquals(dto, actual);
    }

    @Test
    void deleteShouldRemoveItems() {
        List<Integer> ids = List.of(1, 2, 3);
        List<Integer> removedItemIds = List.of(1, 2);
        when(songMetaDataService.deleteAll(ids)).thenReturn(removedItemIds);

        Identifiables<Integer> actual = songMetaDataRestService.deleteMetaData("1,2,3");
        assertEquals(actual, new Identifiables<>(removedItemIds));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deleteShouldReturnEmptyList(String ids) {
        Identifiables<Integer> actual = songMetaDataRestService.deleteMetaData(ids);
        assertEquals(actual.getIds().size(), 0);
    }

}