package org.example.service.core;

import org.example.entity.SongMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongMetaDataService {
    Integer storeMetaData(SongMetaData songMetaData);

    SongMetaData getMetaData(Integer id);

    List<Integer> deleteAll(List<Integer> ids);
}
