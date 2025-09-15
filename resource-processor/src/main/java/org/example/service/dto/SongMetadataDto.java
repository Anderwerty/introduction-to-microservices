package org.example.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongMetadataDto {

    private String name;

    private String artist;

    private String album;

    private String duration;

    private String year;
}
