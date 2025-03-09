package org.example.service.core;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.entity.SongMetadata;
import org.example.repository.MetadataRepository;
import org.example.service.exception.NotFoundException;
import org.example.service.exception.SongAlreadyExistRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SongMetaDataServiceImpl implements SongMetaDataService {

    private final MetadataRepository metadataRepository;

    @Override
    public Integer storeMetaData(SongMetadata songMetaData) {
        if(metadataRepository.existsById(songMetaData.getId())){
            throw new SongAlreadyExistRuntimeException(String.format("Metadata for resource ID=%d already exists",
                    songMetaData.getId()));
        }
        SongMetadata savedSongMetadata = metadataRepository.save(songMetaData);

        return savedSongMetadata.getId();
    }

    @Override
    public SongMetadata getMetaData(Integer id) {
        return metadataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Song metadata for ID=%d not found", id)));
    }

    @Transactional
    @Override
    public List<Integer> deleteAll(List<Integer> ids) {
        Iterable<SongMetadata> existedListSongMetaData = metadataRepository.findAllById(ids);

        List<Integer> existedIds = StreamSupport.stream(existedListSongMetaData.spliterator(), false)
                .map(SongMetadata::getId)
                .toList();
        metadataRepository.deleteAllById(existedIds);

        return existedIds;
    }
}
