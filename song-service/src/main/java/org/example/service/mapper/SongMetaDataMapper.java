package org.example.service.mapper;

import org.example.entity.SongMetadata;
import org.example.service.rest.dto.SongMetaDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SongMetaDataMapper {

    @Mapping(target = "id", expression = "java(java.lang.Integer.parseInt(dto.getId()))")
    SongMetadata mapToEntity(SongMetaDataDto dto);

    @Mapping(target = "id", expression = "java(java.lang.Integer.toString(entity.getId()))")
    SongMetaDataDto mapToDto(SongMetadata entity);
}
