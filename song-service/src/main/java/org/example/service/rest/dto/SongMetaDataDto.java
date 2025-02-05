package org.example.service.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongMetaDataDto {

    @NotNull(message = "Resource ID is required")
    @Positive(message = "Resource ID must be a positive number")
    private Integer id;

    @NotNull(message = "Song name is required")
    @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 symbols")
    private String name;

    @NotNull(message = "Song artist name is required")
    @Size(min = 1, max = 100, message = "Song artist name must be between 1 and 100 symbols")
    private String artist;

    @NotNull(message = "Song album name is required")
    @Size(min = 1, max = 100, message = "Song album name must be between 1 and 100 symbols")
    private String album;

    @NotNull(message = "Song duration is required")
    @Pattern(regexp = "^(0[0-9]|[1-5][0-9]):[0-5][0-9]$", message = "Song duration must have format mm:ss, with leading zeros")
    private String duration;

    @NotNull(message = "Song year publishing is required")
    @Pattern(regexp = "^(19[0-9]{2}|20[0-9]{2})$", message = "Song year publishing must have YYYY format between 1900-2099")
    private String year;
}
