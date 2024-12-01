package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.rest.dto.ErrorMessage;
import org.example.service.rest.dto.SongMetaDataDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

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
                Map.of("getSongMetaData.id","Id [abc] is not int type"));

        mockMvc.perform(get("/songs/" + INVALID_ID).accept(MediaType.APPLICATION_JSON))
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
                .album("")
                .duration("3:35")
                .year("2023")
                .build();

        mockMvc.perform(post("/songs")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(greaterThan(2)));
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

}