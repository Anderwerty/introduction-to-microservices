package org.example.service.rest;

import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongMetaDataRestService {
    Identifiable<Integer> storeMetaData(SongMetaDataDto songMetaDataDto);

    SongMetaDataDto getMetaData(String id);

    List<Integer> deleteMetaData(String ids);
}
