package org.example.service;

import lombok.experimental.UtilityClass;
import org.example.entity.SongMetaData;
import org.example.service.rest.dto.SongMetaDataDto;

@UtilityClass
public class DataUtils {

    public static SongMetaData initSongMetaData(Integer id) {
        SongMetaData songMetaData = new SongMetaData();
        songMetaData.setId(id);
        songMetaData.setAlbum("Земля");
        songMetaData.setArtist("гурт Oкеан Ельзи, вокаліст Святослав Вакарчук");
        songMetaData.setName("Обійми");
        songMetaData.setLength("3:46");
        songMetaData.setYear(2013);
        songMetaData.setResourceId(1);

        return songMetaData;
    }

    public static SongMetaDataDto initSongMetaDataDto() {
        return SongMetaDataDto.builder()
                .album("Земля")
                .artist("гурт Oкеан Ельзи, вокаліст Святослав Вакарчук")
                .name("Обійми")
                .length("3:46")
                .year(2013)
                .resourceId(1)
                .build();
    }
}
