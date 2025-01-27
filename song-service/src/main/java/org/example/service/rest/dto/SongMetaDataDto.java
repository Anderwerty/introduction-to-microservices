package org.example.service.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.service.validator.annotation.SongMetadataValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SongMetadataValidation
public class SongMetaDataDto {

    private String id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private String year;
}
