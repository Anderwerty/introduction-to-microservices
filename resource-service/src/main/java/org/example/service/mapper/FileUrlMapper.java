package org.example.service.mapper;

import org.example.entity.FileUrl;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileUrlMapper {

    FileUrl mapToEntity(org.example.service.dto.FileUrl dto);

    org.example.service.dto.FileUrl mapToDto(FileUrl entity);
}
