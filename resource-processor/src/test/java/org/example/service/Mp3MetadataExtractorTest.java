package org.example.service;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.example.service.dto.SongMetadataDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class Mp3MetadataExtractorTest {
    private static final byte[] FILE_BYTES = readFile("src/test/resources/fortecya-bahmut.mp3");

    private final Mp3Parser mp3Parser = new Mp3Parser();
    private final BodyContentHandler handler = new BodyContentHandler();
    private final Mp3MetadataExtractor extractor = new Mp3MetadataExtractor(mp3Parser, handler);

    @Test
    void testExtractMetaData() {
        SongMetadataDto actual = extractor.extract(FILE_BYTES);
        SongMetadataDto expected = SongMetadataDto.builder()
                .name("Фортеця Бахмут")
                .artist("Антитіла")
                .album("February 2023")
                .duration("03:19")
                .year("2023")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void testGetMime() {
        String mimeType = extractor.getMimeType(FILE_BYTES);
        assertEquals("audio/mpeg", mimeType);
    }

    private static byte[] readFile(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}