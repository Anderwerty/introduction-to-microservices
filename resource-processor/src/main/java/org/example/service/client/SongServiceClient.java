package org.example.service.client;

import org.example.service.dto.Identifiable;
import org.example.service.dto.SongMetadataDto;

public interface SongServiceClient {

    Identifiable<Integer> saveSongMetadata(SongMetadataDto songMetadataDto);

}
