package org.example.service.rest;

import org.example.entity.SongMetadata;
import org.example.service.core.SongMetaDataService;
import org.example.service.mapper.SongMetaDataMapper;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SongMetaDataRestServiceImpl implements SongMetaDataRestService {

    private final SongMetaDataService songMetaDataService;
    private final SongMetaDataMapper songMetaDataMapper;

    public SongMetaDataRestServiceImpl(SongMetaDataService songMetaDataService,
                                       SongMetaDataMapper songMetaDataMapper) {
        this.songMetaDataService = songMetaDataService;
        this.songMetaDataMapper = songMetaDataMapper;
    }

    @Override
    public Identifiable<Integer> storeMetaData(SongMetaDataDto songMetaDataDto) {
        SongMetadata entity = songMetaDataMapper.mapToEntity(songMetaDataDto);
        return new Identifiable<>(songMetaDataService.storeMetaData(entity));
    }

    @Override
    public SongMetaDataDto getMetaData(String id) {
        try {
            Integer identifier = Integer.valueOf(id);
            SongMetadata metaData = songMetaDataService.getMetaData(identifier);
            return songMetaDataMapper.mapToDto(metaData);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Id [%s] is not int type", id), e);
        }
    }

    @Override
    public Identifiables<Integer> deleteMetaData(String idsParameter) {
        if (idsParameter == null || idsParameter.isEmpty()) {
            return new Identifiables<>(Collections.emptyList());
        }

        List<Integer> ids = Arrays.stream(idsParameter.split(","))
                .map(Integer::valueOf)
                .toList();
        if (ids.isEmpty()) {
            return new Identifiables(Collections.emptyList());
        }

        return new Identifiables(songMetaDataService.deleteAll(ids));
    }
}
