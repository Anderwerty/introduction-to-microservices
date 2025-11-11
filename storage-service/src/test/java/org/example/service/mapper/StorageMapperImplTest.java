package org.example.service.mapper;

import org.example.DataUtils;
import org.example.entity.Storage;
import org.example.service.dto.StorageCreationRequest;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.example.DataUtils.ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class StorageMapperImplTest {
    private final StorageMapper storageMapper = new StorageMapperImpl();

    @ParameterizedTest
    @MethodSource("entityData")
    void mapToEntityShouldReturnEntity(StorageType storageType, Storage storage) {
        StorageCreationRequest storageCreationRequest = DataUtils.storageCreationRequest(storageType);
        assertThat(storageMapper.mapToEntity(storageCreationRequest), is(storage));
    }

    @Test
    void mapToEntityShouldReturnNullWhen() {
        assertThat(storageMapper.mapToEntity(null), is(nullValue()));
    }


    @ParameterizedTest
    @MethodSource("dtoData")
    void mapToDtoShouldReturnResponse(org.example.entity.StorageType storageType, StorageDetailsResponse storageDetailsResponse) {
        Storage storage = DataUtils.storage(ID, storageType);
        assertThat(storageMapper.mapToDto(storage), is(storageDetailsResponse));
    }

    @Test
    void mapToDtoShouldReturnNullIfInputNull() {
        assertThat(storageMapper.mapToDto(null), is(nullValue()));
    }

    public static Stream<Arguments> dtoData() {
        return Stream.of(
                Arguments.of(org.example.entity.StorageType.STAGING, DataUtils.storageDetailsResponse(StorageType.STAGING)),
                Arguments.of(org.example.entity.StorageType.PERMANENT, DataUtils.storageDetailsResponse(StorageType.PERMANENT)),
                Arguments.of(null, DataUtils.storageDetailsResponse(null))
        );
    }

    public static Stream<Arguments> entityData() {
        return Stream.of(
                Arguments.of(StorageType.STAGING, DataUtils.storage(org.example.entity.StorageType.STAGING)),
                Arguments.of(StorageType.PERMANENT, DataUtils.storage( org.example.entity.StorageType.PERMANENT)),
                Arguments.of(null, DataUtils.storage(null))
        );
    }

}