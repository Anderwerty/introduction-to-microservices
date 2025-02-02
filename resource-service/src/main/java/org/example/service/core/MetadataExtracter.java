package org.example.service.core;

import org.example.service.dto.SongMetadataDto;

public interface MetadataExtracter {
    SongMetadataDto extract(byte[] fileBytes, Integer id);

    String getMimeType(byte[] fileBytes);
}
