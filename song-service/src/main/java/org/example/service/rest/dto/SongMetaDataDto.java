package org.example.service.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongMetaDataDto {

    private String name;

    private String artist;

    private String album;

    private String length;

    private Integer resourceId;

    private Integer year;
}
