package org.example.service;

import org.example.service.dto.SongMetadataDto;

public interface MetadataExtractor {
    SongMetadataDto extract(byte[] fileBytes);

    String getMimeType(byte[] fileBytes);
}