package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApplicationConfig;
import org.example.service.dto.ErrorMessage;
import org.example.service.dto.Identifiables;
import org.example.service.dto.SongMetadataDto;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.example.DataUtils.FILE_BYTES;
import static org.example.DataUtils.readFile;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ApplicationConfig.class)
@AutoConfigureMockMvc
class ResourceControllerTest {
    private static final String EXISTED_ID = "1";
    private static final String NOT_EXISTED_ID = "123";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    void getResourceIfDataExist() throws Exception {
        mockMvc.perform(get("/resources/" + EXISTED_ID)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().bytes(FILE_BYTES))
                .andExpect(content().contentTypeCompatibleWith("audio/mpeg"));
    }

    @Test
    void getSongMetaDataIfDataNotExist() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(404, "Resources with id=[123] doesn't exist");

        mockMvc.perform(get("/resources/" + NOT_EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("idToErrorMessage")
    void getSongMetaDataIfIdNotValid(String id, ErrorMessage errorMessage) throws Exception {
        mockMvc.perform(get("/resources/" + id).contentType("audio/mpeg"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorMessage)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private static Stream<Arguments> idToErrorMessage() {
        return Stream.of(
                Arguments.of("abc", new ErrorMessage(400, "Validation error", Map.of("getBinaryAudioData.id", "Id [abc] is not int type"))),
                Arguments.of("-1", new ErrorMessage(400, "Validation error", Map.of("getBinaryAudioData.id", "Id [-1] is not positive")))
        );
    }

    @Test
    @Order(1) // use to be sure about id value
    void createMetadata() throws Exception {

        byte[] fileBytes = readFile("src/test/resources/fortecya-bahmut.mp3");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        SongMetadataDto request = SongMetadataDto.builder()
                .id(5)
                .artist("Антитіла")
                .name("Фортеця Бахмут")
                .album("February 2023")
                .duration("03:19")
                .year("2023")
                .build();

        mockServer.expect(requestTo("http://song-service/songs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().string(mapper.writeValueAsString(request)))
                .andRespond(withSuccess("{ \"id\" : 5}", MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/resources")
                        .content(fileBytes)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(greaterThan(2)));
    }

    @Test
    void deleteShouldReturnListOfIds() throws Exception {
        System.out.println(mapper.writeValueAsString(new Identifiables<>(Arrays.asList(2, 3))));
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo("http://song-service/songs?id=2,3"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(MockRestRequestMatchers.queryParam("id","2,3"))
                .andRespond(withSuccess("{ \"ids\" : [2,3]}", MediaType.APPLICATION_JSON));

        mockMvc.perform(delete("/resources")
                        .param("id", "2,3,10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(2)))
                .andExpect(jsonPath("$.ids.[0]", is(2)))
                .andExpect(jsonPath("$.ids.[1]", is(3)));
    }

    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistQueryParameter() throws Exception {
        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(0)));
    }

    @Test
    void deleteShouldReturnEmptyListOfIdsForNotExistIds() throws Exception {
        ErrorMessage errorMessage = new ErrorMessage(400, "Too long ids parameter length [31]");

        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "200,300,400,500,600,700,800,900")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorMessage))
                )
                .andExpect(status().isBadRequest());
    }

}