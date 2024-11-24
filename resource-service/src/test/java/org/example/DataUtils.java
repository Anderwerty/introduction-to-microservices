package org.example;

import lombok.experimental.UtilityClass;
import org.example.entity.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class DataUtils {
    public static final byte[] FILE_BYTES = {49, 50, 51, 52};
    public static final Resource RESOURCE_WITHOUT_ID = new Resource(FILE_BYTES);
    public static final Resource RESOURCE_WITH_ID = new Resource(1, FILE_BYTES);
    public static final Resource RESOURCE_WITH_ID_2 = new Resource(2, FILE_BYTES);

    public static final String CONTENT_TYPE = "audio/mpeg";

    public static final String FILENAME = "music.mp3";
    public static final MultipartFile MULTIPART_FILE =
            new MockMultipartFile(FILENAME, FILENAME, CONTENT_TYPE, FILE_BYTES);


    public static byte[] readFile(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
