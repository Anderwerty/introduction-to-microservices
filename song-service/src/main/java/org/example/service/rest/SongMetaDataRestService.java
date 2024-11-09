package org.example.service.rest;

import org.example.service.exception.NotFoundException;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;

import java.util.List;

public interface SongMetaDataRestService {
    Identifiable<Integer> storeMetaData(SongMetaDataDto songMetaDataDto);

    SongMetaDataDto getMetaData(String id) throws IllegalArgumentException, NotFoundException;

    List<Integer> deleteMetaData(String ids) throws IllegalArgumentException;
}
