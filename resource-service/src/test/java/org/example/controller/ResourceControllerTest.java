package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApplicationConfig;
import org.example.service.dto.SimpleErrorResponse;
import org.example.service.dto.ValidationErrorResponse;
import org.example.service.dto.Identifiables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.example.DataUtils.FILE_BYTES;
import static org.example.DataUtils.readFile;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
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

    @MockitoBean
    private DiscoveryClient discoveryClient;

    @Value("${song.service.name}")
    private String songServiceName;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init(){
        when(discoveryClient.getInstances(songServiceName)).thenReturn(
                Collections.singletonList(new ServiceInstance() {
                    @Override
                    public String getServiceId() {
                        return songServiceName;
                    }

                    @Override
                    public String getHost() {
                        return "song-service";
                    }

                    @Override
                    public int getPort() {
                        return 8080;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public URI getUri() {
                        try {
                            return new URI("http", null, "song-service", 8080, "/songs", null, null);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException("Invalid URI", e);
                        }
                    }

                    @Override
                    public Map<String, String> getMetadata() {
                        return null;
                    }

                })
        );
    }

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
        SimpleErrorResponse errorResponse = new SimpleErrorResponse("404", "Resource with ID=123 not found");

        mockMvc.perform(get("/resources/" + NOT_EXISTED_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(errorResponse)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdNull() throws Exception {
        String id = null;
        SimpleErrorResponse errorResponse = new SimpleErrorResponse("400",  "Invalid value 'null' for ID. Must be a positive integer");

        mockMvc.perform(get("/resources/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorResponse)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSongMetaDataIfIdBlank() throws Exception {
        String id = "  ";
        SimpleErrorResponse validationErrorResponse = new SimpleErrorResponse("400",  "Invalid value '  ' for ID. Must be a positive integer");

        mockMvc.perform(get("/resources/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(validationErrorResponse)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("idToErrorMessage")
    void getSongMetaDataIfIdNotValid(String id, SimpleErrorResponse errorResponse) throws Exception {
        mockMvc.perform(get("/resources/" + id).contentType("audio/mpeg"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorResponse)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private static Stream<Arguments> idToErrorMessage() {
        return Stream.of(
                Arguments.of("abc", new SimpleErrorResponse(400,  "Invalid value 'abc' for ID. Must be a positive integer")),
                Arguments.of("-1", new SimpleErrorResponse(400, "Invalid value '-1' for ID. Must be a positive integer"))
        );
    }

    @Test
    void createMetadata() throws Exception {

        byte[] fileBytes = readFile("src/test/resources/fortecya-bahmut.mp3");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(requestTo("http://song-service:8080/songs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.jsonPath("$.artist", is("Антитіла")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.name", is("Фортеця Бахмут")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.album", is("February 2023")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.duration", is("03:19")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.year", is("2023")))
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
    void createMetadataToGetConflict() throws Exception {

        byte[] fileBytes = readFile("src/test/resources/fortecya-bahmut.mp3");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        SimpleErrorResponse validationErrorResponse = new SimpleErrorResponse(409, null);

        mockServer.expect(requestTo("http://song-service:8080/songs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.jsonPath("$.artist", is("Антитіла")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.name", is("Фортеця Бахмут")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.album", is("February 2023")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.duration", is("03:19")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.year", is("2023")))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(validationErrorResponse)));

        mockMvc.perform(post("/resources")
                        .content(fileBytes)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andExpect(content().json(mapper.writeValueAsString(validationErrorResponse)));
    }

    @Test
    void createMetadataWithNotExpectedContentTypeToGetBadRequest() throws Exception {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(400,"Invalid file format: application/pdf. Only MP3 files are allowed" );
        byte[] fileBytes = readFile("src/test/resources/music.pdf");
        MockRestServiceServer.createServer(restTemplate);

        mockMvc.perform(post("/resources")
                        .content(fileBytes)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    void createMetadataWithNotValidFileToGetBadRequest() throws Exception {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(400,null );
        ValidationErrorResponse expectedValidationErrorResponse = new ValidationErrorResponse(400, validationErrorResponse.getDetails());
        byte[] fileBytes = readFile("src/test/resources/invalid-sample-with-missed-tags.mp3");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(requestTo("http://song-service:8080/songs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.jsonPath("$.artist", is("")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.name", is("Valid Title")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.album", is("")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.duration", is("00:07")))
                .andExpect(MockRestRequestMatchers.jsonPath("$.year", is("2025")))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(validationErrorResponse)));
        mockMvc.perform(post("/resources")
                        .content(fileBytes)
                        .contentType("audio/mpeg")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expectedValidationErrorResponse)));
    }

    @Test
    void deleteShouldReturnListOfIds() throws Exception {
        System.out.println(mapper.writeValueAsString(new Identifiables<>(Arrays.asList(2, 3))));
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo("http://song-service:8080/songs?id=2,3"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(MockRestRequestMatchers.queryParam("id", "2,3"))
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
    void deleteShouldReturnBadRequestWhenIdQueryParameterTooLong() throws Exception {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(400, "Too long ids parameter length 31");

        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "200,300,400,500,600,700,800,900")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(errorResponse))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldReturnBadRequestWhenIdIsNotNumber() throws Exception {
        SimpleErrorResponse validationErrorResponse = new SimpleErrorResponse(400, "Id abc is not a number");

        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "abc")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(validationErrorResponse))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldReturnBadRequestWhenIdIsNotPositiveNumber() throws Exception {
        SimpleErrorResponse validationErrorResponse = new SimpleErrorResponse(400, "Id %s is not positive int");

        mockMvc.perform(delete("/resources")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "0,-1")
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(validationErrorResponse))
                )
                .andExpect(status().isBadRequest());
    }

}