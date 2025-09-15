package org.example.service;

import org.example.service.dto.SongMetadataDto;

public interface MetadataExtracter {
    SongMetadataDto extract(byte[] fileBytes);

    String getMimeType(byte[] fileBytes);
}