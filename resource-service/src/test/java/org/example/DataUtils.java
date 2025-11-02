package org.example;

import lombok.experimental.UtilityClass;
import org.example.entity.FileState;
import org.example.entity.Resource;
import org.example.entity.FileUrl;
import org.example.service.dto.SongMetadataDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class DataUtils {
    public static final byte[] FILE_BYTES = {49, 50, 51, 52};

    public static final String BUCKET_NAME = "dummy-bucket";
    public static final String PATH = "staging-files";
    public static final String GENERATED_KEY1 = "3116a6ba-aa62-4f3e-b16a-1b365d34a001";
    public static final String GENERATED_KEY2 = "3116a6ba-aa62-4f3e-b16a-1b365d34a002";
    public static final String DUMMY_URL1 = "http://localhost:4566/" + BUCKET_NAME + "/" + GENERATED_KEY1;
    public static final String DUMMY_URL2 = "http://localhost:4566/" + BUCKET_NAME + "/" + GENERATED_KEY2;
    public static final Resource RESOURCE_WITHOUT_ID = new Resource(new FileUrl(DUMMY_URL1, BUCKET_NAME, GENERATED_KEY1));
    public static final Resource RESOURCE_WITH_ID = new Resource(1, new FileUrl(DUMMY_URL1, BUCKET_NAME, GENERATED_KEY1), FileState.STAGING);
    public static final Resource RESOURCE_WITH_ID_2 = new Resource(2, new FileUrl(DUMMY_URL2, BUCKET_NAME, GENERATED_KEY2), FileState.STAGING);


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

    public static org.example.service.dto.FileUrl initFileUrlDto(String bucketName, String key, String fullUrl) {
        return org.example.service.dto.FileUrl.builder()
                .bucketName(bucketName)
                .key(key)
                .fullUrl(fullUrl)
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
