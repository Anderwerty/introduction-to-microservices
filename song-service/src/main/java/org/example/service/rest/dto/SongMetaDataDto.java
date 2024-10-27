package org.example.service.rest.dto;

import lombok.Data;

@Data
public class SongMetaDataDto {

    private String name;

    private String artist;

    private String album;

    private String length;

    private Integer resourceId;

    private Integer year;
}
