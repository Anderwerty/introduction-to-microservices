package org.example.service;

import lombok.AllArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.example.service.dto.SongMetadataDto;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@AllArgsConstructor
public class Mp3MetadataExtracter implements MetadataExtracter {
    private final Mp3Parser mp3Parser;
    private final BodyContentHandler handler;

    @Override
    public SongMetadataDto extract(byte[] file) {
        try (InputStream inputstream = new ByteArrayInputStream(file)) {
            ParseContext context = new ParseContext();
            Metadata metadata = new Metadata();

            mp3Parser.parse(inputstream, handler, metadata, context);

            return SongMetadataDto.builder()
                    .name(metadata.get("dc:title"))
                    .artist(metadata.get(XMPDM.ARTIST))
                    .album(metadata.get(XMPDM.ALBUM))
                    .duration(convertSecondsToDuration(metadata.get(XMPDM.DURATION)))
                    .year(metadata.get(XMPDM.RELEASE_DATE).substring(0, 4))
                    .build();

        } catch (IOException | SAXException | TikaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMimeType(byte[] fileBytes) {
        Tika tika = new Tika();
        return tika.detect(fileBytes);
    }

    private static String convertSecondsToDuration(String time) {
        int totalTime = (int)Double.parseDouble(time);
        int seconds = totalTime % 60;
        int minutes = totalTime / 60;

        String secondsStr = convertWithLeadingZero(seconds);
        String minutesStr = convertWithLeadingZero(minutes);

        return minutesStr + ":" + secondsStr;
    }

    private static String convertWithLeadingZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }
}
