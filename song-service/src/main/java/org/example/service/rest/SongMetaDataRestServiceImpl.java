package org.example.service.rest;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.entity.SongMetadata;
import org.example.service.core.SongMetaDataService;
import org.example.service.mapper.SongMetaDataMapper;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SongMetaDataRestServiceImpl implements SongMetaDataRestService {

    private final SongMetaDataService songMetaDataService;
    private final SongMetaDataMapper songMetaDataMapper;
    private final int idsParameterLengthLimit;

    public SongMetaDataRestServiceImpl(SongMetaDataService songMetaDataService,
                                       SongMetaDataMapper songMetaDataMapper,
                                       @Value("${ids.parameter.length.limit}")int idsParameterLengthLimit) {
        this.songMetaDataService = songMetaDataService;
        this.songMetaDataMapper = songMetaDataMapper;
        this.idsParameterLengthLimit = idsParameterLengthLimit;
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
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(String.format("Id [%s] is not int type", id), e);
        }
    }

    @Override
    public List<Integer> deleteMetaData(String idsParameter) {
        if (idsParameter == null || idsParameter.isEmpty()) {
            return Collections.emptyList();
        }

        validateIdsParameter(idsParameter);

        List<Integer> ids = Arrays.stream(idsParameter.split(",")).map(String::trim)
                .filter(NumberUtils::isCreatable)
                .map(Integer::valueOf)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return songMetaDataService.deleteAll(ids);
    }

    private void validateIdsParameter(String idsParameter) {
        int length = idsParameter.length();
        if (length >= idsParameterLengthLimit) {
            throw new IllegalArgumentException(String.format("Too long ids parameter length [%d]", length));
        }
    }
}
