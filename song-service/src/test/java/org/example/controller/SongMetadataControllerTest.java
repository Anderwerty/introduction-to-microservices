package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.rest.dto.ErrorMessage;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Map;
import java.util.stream.Stream;

import static org.example.service.DataUtils.initSongMetaDataDto;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SongMetadataControllerTest {
    private static final String EXISTED_ID = "1";
    private static final String NOT_EXISTED_ID = "123";
    private static final String INVALID_ID = "abc";

    private static final String NEGATIVE_ID = "-1";

    private static final String BLANK_ID = "  ";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getSongMetaDataIfDataExist() throws Exception {
        mockMvc.perform(get("/songs/" + EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(initSongMetaDataDto(1))))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfDataNotExist() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(404, "Song metadata with id=[123] doesn't exist");

        mockMvc.perform(get("/songs/" + NOT_EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdNotValid() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Validation error",
                Map.of("getSongMetaData.id", "Id [abc] is not int type"));

        mockMvc.perform(get("/songs/" + INVALID_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdNegative() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Validation error",
                Map.of("getSongMetaData.id", "Id [-1] is not positive"));

        mockMvc.perform(get("/songs/" + NEGATIVE_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdBlank() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Validation error",
                Map.of("getSongMetaData.id", "Id is null or blank"));

        mockMvc.perform(get("/songs/" + BLANK_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void createMetadata() throws Exception {
        SongMetaDataDto request = SongMetaDataDto.builder()
                .id(3)
                .artist("Антитіла")
                .name("Фортеця Бахмут")
                .album("February 2023")
                .duration("03:35")
                .year("2023")
                .build();

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(greaterThan(2)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createMetadataWithIdAlreadyExists() throws Exception {
        SongMetaDataDto request = SongMetaDataDto.builder()
                .id(1)
                .artist("Антитіла")
                .name("Фортеця Бахмут")
                .album("February 2023")
                .duration("03:35")
                .year("2023")
                .build();

        ErrorMessage errorMessage = new ErrorMessage(409, "Metadata for song with id [1] already exists");

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))

                )
                .andExpect(status().isConflict())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @MethodSource("songMetaData")
    void createMetadataShouldProvideErrorMessage(SongMetaDataDto request) throws Exception {
        Map<String, String> details = Map.of("duration", "Format mm:ss, with leading zeros.",
                "artist", "1-100 characters text",
                "year", "YYYY format between 1900-2099.",
                "album", "1-100 characters text",
                "name", "1-100 characters text",
                "id", "Numeric, must match an existing Resource ID.");
        ErrorMessage errorMessage = new ErrorMessage(400, "Validation error", details);

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))

                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deleteShouldReturnListOfIds() throws Exception {
        mockMvc.perform(delete("/songs")
                        .param("id", "2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(1)))
                .andExpect(jsonPath("$.ids.[0]", is(2)));
    }

    @Test
    void deleteShouldReturnEmptyListForNotExistResource() throws Exception {
        mockMvc.perform(delete("/songs")
                        .param("id", "200,300")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(0)));
    }

    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistQueryParameter() throws Exception {
        mockMvc.perform(delete("/songs")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(0)));
    }


    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistIds() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Too long ids parameter length [31]");

        mockMvc.perform(delete("/songs")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "200,300,400,500,600,700,800,900")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorMessage))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldReturnErrorIfIdsContainsNegativeValue() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Id [-200] is not a positive int");

        mockMvc.perform(delete("/songs")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "200,-200")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorMessage))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldReturnErrorIfIdsContainsNotNumericValue() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Id [abc] is not a number");

        mockMvc.perform(delete("/songs")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "abc")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorMessage))
                )
                .andExpect(status().isBadRequest());
    }

    public static Stream<Arguments> songMetaData() {
        return Stream.of(
                Arguments.of(SongMetaDataDto.builder()
                        .id(null)
                        .artist(null)
                        .name(null)
                        .album(null)
                        .duration(null)
                        .year(null)
                        .build()),
                Arguments.of(SongMetaDataDto.builder()
                        .id(null)
                        .artist("")
                        .name("")
                        .album("")
                        .duration("")
                        .year("")
                        .build())
                );
    }

}