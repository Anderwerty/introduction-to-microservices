package org.example.service.mapper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class FileUrlMapperTest {

    private final FileUrlMapper mapper = new FileUrlMapperImpl();

    @Test
    void shouldMapDtoToEntity() {
        org.example.service.dto.FileUrl dto = org.example.service.dto.FileUrl.builder()
                .fullUrl("https://example.com/file.mp3")
                .bucketName("my-bucket")
                .key("file.mp3")
                .build();

        org.example.entity.FileUrl entity = mapper.mapToEntity(dto);

        assertAll(
                () -> assertThat(entity, is(notNullValue())),
                () -> assertThat(entity.getFullUrl(), is(dto.getFullUrl())),
                () -> assertThat(entity.getBucketName(), is(dto.getBucketName())),
                () -> assertThat(entity.getKey(), is(dto.getKey())));
    }

    @Test
    void shouldMapEntityToDto() {

        org.example.entity.FileUrl entity = new org.example.entity.FileUrl();
        entity.setFullUrl("https://example.com/file.mp3");
        entity.setBucketName("my-bucket");
        entity.setKey("file.mp3");

        org.example.service.dto.FileUrl dto = mapper.mapToDto(entity);

        assertAll(
                () -> assertThat(dto, is(notNullValue())),
                () -> assertThat(dto.getFullUrl(), is(entity.getFullUrl())),
                () -> assertThat(dto.getBucketName(), is(entity.getBucketName())),
                () -> assertThat(dto.getKey(), is(entity.getKey())));
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.mapToEntity(null), is(nullValue()));
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.mapToDto(null), is(nullValue()));
    }
}