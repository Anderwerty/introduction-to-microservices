package org.example.service.core;

import org.example.entity.SongMetadata;
import org.example.service.exception.NotFoundException;

import java.util.List;

public interface SongMetaDataService {
    Integer storeMetaData(SongMetadata songMetaData);

    SongMetadata getMetaData(Integer id) throws NotFoundException;

    List<Integer> deleteAll(List<Integer> ids);
}
