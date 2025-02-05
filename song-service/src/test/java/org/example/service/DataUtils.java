package org.example.service;

import lombok.experimental.UtilityClass;
import org.example.entity.SongMetadata;
import org.example.service.rest.dto.SongMetaDataDto;

@UtilityClass
public class DataUtils {

    public static SongMetadata initSongMetaData(Integer id) {
        return initSongMetaData(id, 2013);
    }

    public static SongMetadata initSongMetaData(Integer id, Integer year) {
        SongMetadata songMetaData = new SongMetadata();
        songMetaData.setId(id);
        songMetaData.setAlbum("Земля");
        songMetaData.setArtist("гурт Oкеан Ельзи, вокаліст Святослав Вакарчук");
        songMetaData.setName("Обійми");
        songMetaData.setDuration("3:46");
        songMetaData.setYear(year);

        return songMetaData;
    }

    public static SongMetaDataDto initSongMetaDataDto(Integer id) {
        return initSongMetaDataDto(id, "2013");
    }

    public static SongMetaDataDto initSongMetaDataDto(Integer id, String year) {
        return SongMetaDataDto.builder()
                .id(id)
                .album("Земля")
                .artist("гурт Oкеан Ельзи, вокаліст Святослав Вакарчук")
                .name("Обійми")
                .duration("3:46")
                .year(year)
                .build();
    }
}
