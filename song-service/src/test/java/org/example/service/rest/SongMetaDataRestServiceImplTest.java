package org.example.service.rest;

import org.example.entity.SongMetaData;
import org.example.service.DataUtils;
import org.example.service.core.SongMetaDataService;
import org.example.service.mapper.SongMetaDataMapper;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.service.DataUtils.initSongMetaData;
import static org.example.service.DataUtils.initSongMetaDataDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongMetaDataRestServiceImplTest {
    @Mock
    private SongMetaDataService songMetaDataService;

    @Mock
    private SongMetaDataMapper songMetaDataMapper;

    @InjectMocks
    private SongMetaDataRestServiceImpl songMetaDataRestService;

    @Test
    void storeMetaDataShouldStoreData() {
        SongMetaData entity = initSongMetaData(null);
        SongMetaDataDto dto = initSongMetaDataDto();
        when(songMetaDataMapper.mapToEntity(dto)).thenReturn(entity);
        when(songMetaDataService.storeMetaData(entity)).thenReturn(1);

        Identifiable<Integer> identifiable = songMetaDataRestService.storeMetaData(dto);
        assertEquals(new Identifiable<>(1), identifiable);
    }

    @Test
    void getMetaDataShouldThrowExceptionWhenIdIsNotInteger() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> songMetaDataRestService.getMetaData("abc"));
        assertEquals("Id [abc] is not int type", exception.getMessage());
    }

    @Test
    void getMetaDataShouldReturnSongMetaData() {
        SongMetaData entity = initSongMetaData(1);
        SongMetaDataDto dto = initSongMetaDataDto();
        when(songMetaDataService.getMetaData(1)).thenReturn(entity);
        when(songMetaDataMapper.mapToDto(entity)).thenReturn(dto);

        SongMetaDataDto actual = songMetaDataRestService.getMetaData("1");
        assertEquals(dto, actual);
    }

}