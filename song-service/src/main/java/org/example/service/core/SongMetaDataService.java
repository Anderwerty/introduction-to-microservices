package org.example.service.core;

import org.example.entity.SongMetadata;

import java.util.List;

public interface SongMetaDataService {
    Integer storeMetaData(SongMetadata songMetaData);

    SongMetadata getMetaData(Integer id);

    List<Integer> deleteAll(List<Integer> ids);
}
