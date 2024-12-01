package org.example.service.mapper;

import org.example.entity.SongMetadata;
import org.example.service.rest.dto.SongMetaDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SongMetaDataMapper {

    SongMetadata mapToEntity(SongMetaDataDto dto);

    SongMetaDataDto mapToDto(SongMetadata entity);
}
