package org.example;

import lombok.experimental.UtilityClass;
import org.example.entity.Resource;
import org.example.service.dto.SongMetadataDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class DataUtils {
    public static final byte[] FILE_BYTES = {49, 50, 51, 52};

    public static final String GENERATED_KEY1 = "3116a6ba-aa62-4f3e-b16a-1b365d34a001";
    public static final String GENERATED_KEY2 = "3116a6ba-aa62-4f3e-b16a-1b365d34a002";
    public static final String DUMMY_URL1 = "dummy-bucket/" + GENERATED_KEY1;
    public static final String DUMMY_URL2 = "dummy-bucket/" + GENERATED_KEY1;
    public static final Resource RESOURCE_WITHOUT_ID = new Resource(DUMMY_URL1);
    public static final Resource RESOURCE_WITH_ID = new Resource(1, DUMMY_URL1);
    public static final Resource RESOURCE_WITH_ID_2 = new Resource(2, DUMMY_URL2);


    public static SongMetadataDto initSongMetaDataDto(Integer id) {
        return SongMetadataDto.builder()
                .id(id)
                .album("Земля")
                .artist("гурт Oкеан Ельзи, вокаліст Святослав Вакарчук")
                .name("Обійми")
                .duration("3:46")
                .year("2013")
                .build();
    }

    public static byte[] readFile(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
