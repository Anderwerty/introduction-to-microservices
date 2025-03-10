package org.example.service.rest;

import org.example.service.exception.NotFoundException;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;
import org.example.service.rest.dto.SongMetaDataDto;

public interface SongMetaDataRestService {
    Identifiable<Integer> storeMetaData(SongMetaDataDto songMetaDataDto);

    SongMetaDataDto getMetaData(String id) throws NotFoundException;

    Identifiables<Integer> deleteMetaData(String ids);
}
