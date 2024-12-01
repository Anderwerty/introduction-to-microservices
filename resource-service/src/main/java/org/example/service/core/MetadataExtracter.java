package org.example.service.core;

import org.example.service.dto.SongMetadataDto;

public interface MetadataExtracter {
    public SongMetadataDto extract(byte[] fileBytes, Integer id);

    String getMimeType(byte[] fileBytes);
}
