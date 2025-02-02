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
    public static final Resource RESOURCE_WITHOUT_ID = new Resource(FILE_BYTES);
    public static final Resource RESOURCE_WITH_ID = new Resource(1, FILE_BYTES);
    public static final Resource RESOURCE_WITH_ID_2 = new Resource(2, FILE_BYTES);


    public static SongMetadataDto initSongMetaDataDto(Integer id) {
        return SongMetadataDto.builder()
                .id(Integer.toString(id))
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
