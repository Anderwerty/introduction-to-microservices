package org.example.service.mapper;

import org.example.entity.Storage;
import org.example.service.dto.StorageCreationRequest;
import org.example.service.dto.StorageDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StorageMapper {

    Storage mapToEntity(StorageCreationRequest dto);

    StorageDetailsResponse mapToDto(Storage entity);
}
