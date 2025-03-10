package org.example.service.mapper;

import org.example.entity.SongMetadata;
import org.example.service.DataUtils;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SongMetadataMapperImplTest {

    private final SongMetaDataMapper songMetaDataMapper = new SongMetaDataMapperImpl();

    @Test
    void mapToEntityShouldReturnEntity() {
        SongMetadata songMetaData = songMetaDataMapper.mapToEntity(DataUtils.initSongMetaDataDto(1));

        assertEquals(DataUtils.initSongMetaData(1), songMetaData);
    }

    @Test
    void mapToEntityShouldReturnNullIfParameterNull() {
        SongMetadata songMetaData = songMetaDataMapper.mapToEntity(null);

        assertNull(songMetaData);
    }

    @Test
    void mapToEntityShouldReturnEntityIfNullYear() {
        SongMetadata songMetaData = songMetaDataMapper.mapToEntity(DataUtils.initSongMetaDataDto(1, null));

        assertEquals(DataUtils.initSongMetaData(1, null), songMetaData);
    }

    @Test
    void mapToDtoShouldReturnDto() {
        SongMetaDataDto songMetaData = songMetaDataMapper.mapToDto(DataUtils.initSongMetaData(1));

        assertEquals(DataUtils.initSongMetaDataDto(1), songMetaData);
    }

    @Test
    void mapToDtoShouldReturnNullIfParameterNull() {
        SongMetaDataDto songMetaData = songMetaDataMapper.mapToDto(null);

        assertNull(songMetaData);
    }

    @Test
    void mapToDtoShouldReturnDtoIfYeIsNull() {
        SongMetaDataDto songMetaData = songMetaDataMapper.mapToDto(DataUtils.initSongMetaData(1, null));

        assertEquals(DataUtils.initSongMetaDataDto(1, null), songMetaData);
    }

}
