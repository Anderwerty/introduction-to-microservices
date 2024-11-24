package org.example.service.mapper;

import org.example.entity.SongMetadata;
import org.example.service.DataUtils;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SongMetadataMapperImplTest {

    private final SongMetaDataMapper songMetaDataMapper = new SongMetaDataMapperImpl();

    @Test
    void mapToEntityShouldReturnEntity(){
        SongMetadata songMetaData = songMetaDataMapper.mapToEntity(DataUtils.initSongMetaDataDto());

        assertEquals(songMetaData, DataUtils.initSongMetaData(null));
    }

    @Test
    void mapToDtoShouldReturnDto(){
        SongMetaDataDto songMetaData = songMetaDataMapper.mapToDto(DataUtils.initSongMetaData(1));

        assertEquals(songMetaData, DataUtils.initSongMetaDataDto());
    }

}
