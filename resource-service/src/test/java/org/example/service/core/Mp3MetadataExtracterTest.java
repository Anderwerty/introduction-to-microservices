package org.example.service.core;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.example.service.dto.SongMetadataDto;
import org.junit.jupiter.api.Test;

import static org.example.DataUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Mp3MetadataExtracterTest {
    private static final byte[] FILE_BYTES = readFile("src/test/resources/fortecya-bahmut.mp3");

    private final Mp3Parser mp3Parser = new Mp3Parser();
    private final BodyContentHandler handler = new BodyContentHandler();
    private final Mp3MetadataExtracter extracter = new Mp3MetadataExtracter(mp3Parser, handler);

    @Test
    void testExtractMetaData() {
        SongMetadataDto actual = extracter.extract(FILE_BYTES, 1);
        SongMetadataDto expected = SongMetadataDto.builder()
                .id(1)
                .name("Фортеця Бахмут")
                .artist("Антитіла")
                .album("February 2023")
                .duration("03:19")
                .year("2023")
                .build();

        assertEquals(actual, expected);
    }

    @Test
    void testGetMime() {
        String mimeType = extracter.getMimeType(FILE_BYTES);
        assertEquals(mimeType, "audio/mpeg");
    }
}