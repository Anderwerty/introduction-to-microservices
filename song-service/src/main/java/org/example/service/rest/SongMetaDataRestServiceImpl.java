package org.example.service.rest;

import lombok.AllArgsConstructor;
import org.example.service.core.SongMetaDataService;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SongMetaDataRestServiceImpl implements SongMetaDataRestService {

    private final SongMetaDataService songMetaDataService;
    @Override
    public Identifiable<Integer> storeMetaData(SongMetaDataDto songMetaDataDto) {
        return null;
    }

    @Override
    public SongMetaDataDto getMetaData(String id) {
        return null;
    }

    @Override
    public List<Integer> deleteMetaData(String ids) {
        return null;
    }
}
