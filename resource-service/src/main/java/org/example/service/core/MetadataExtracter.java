package org.example.service.core;

import org.example.service.rest.dto.SongMetadataDto;

public interface MetadataExtracter {
    public SongMetadataDto extract(byte[] fileBytes, Integer id);

    String getMimeType(byte[] fileBytes);
}
