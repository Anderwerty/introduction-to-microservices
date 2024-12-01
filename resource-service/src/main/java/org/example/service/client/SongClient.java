package org.example.service.client;

import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;
import org.example.service.dto.SongMetadataDto;

import java.util.List;

public interface SongClient {

    Identifiable<Integer> saveSongMetadata(SongMetadataDto songMetadataDto);

    Identifiables<Integer> deleteSongsMetadata(List<Integer> ids);
}
