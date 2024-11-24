package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApplicationConfig;
import org.example.service.rest.dto.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.example.DataUtils.FILE_BYTES;
import static org.example.DataUtils.readFile;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ApplicationConfig.class)
@AutoConfigureMockMvc
class ResourceControllerTest {
    private static final String EXISTED_ID = "1";
    private static final String NOT_EXISTED_ID = "123";
    private static final String INVALID_ID = "abc";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getResourceIfDataExist() throws Exception {
        mockMvc.perform(get("/resources/" + EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().bytes(FILE_BYTES))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfDataNotExist() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(404, "Resources with id=[123] doesn't exist");

        mockMvc.perform(get("/resources/" + NOT_EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdNotValid() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Id [abc] is not int type");

        mockMvc.perform(get("/resources/" + INVALID_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void createMetadata() throws Exception {
        byte[] fileBytes = readFile("src/test/resources/fortecya-bahmut.mp3");

        mockMvc.perform(post("/resources")
                        .content(fileBytes)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(greaterThan(2)));
    }

    @Test
    void deleteShouldReturnListOfIds() throws Exception {
        mockMvc.perform(delete("/resources")
                        .param("ids", "2,3,10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0]", is(2)))
                .andExpect(jsonPath("$.[1]", is(3)));
    }

    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistQueryParameter() throws Exception {
        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistIds() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Too long ids parameter length [31]");

        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("ids", "200,300,400,500,600,700,800,900")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorMessage))
                )
                .andExpect(status().isBadRequest());
    }

}