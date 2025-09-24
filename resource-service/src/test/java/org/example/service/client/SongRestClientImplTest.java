package org.example.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DataUtils;
import org.example.config.ApplicationConfig;
import org.example.config.RabbitProducerConfig;
import org.example.service.dto.*;
import org.example.service.exception.ConflictRuntimeException;
import org.example.service.exception.NotValidSongMetaDataRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class SongRestClientImplTest {

    @Autowired
    private SongRestClientImpl songRestClient;

    @Autowired
    @Qualifier("song.service.rest.template")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void saveSongMetadataShouldSaveData() throws Exception {
        SongMetadataDto metadataDto = DataUtils.initSongMetaDataDto(1);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://song-service/songs")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(metadataDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new Identifiable(1))));

        Identifiable<Integer> actual = songRestClient.saveSongMetadata(metadataDto);
        Identifiable<Integer> expected = new Identifiable<>(1);

        assertEquals(expected, actual);
    }

    @Test
    void saveSongMetadataShouldNotSaveNotValidData() throws Exception {
        SongMetadataDto metadataDto = DataUtils.initSongMetaDataDto(null);
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(400, Map.of("id", "ID must be not null"));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://song-service/songs")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(metadataDto)))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(validationErrorResponse)));

        NotValidSongMetaDataRuntimeException exception =
                assertThrows(NotValidSongMetaDataRuntimeException.class,
                        () -> songRestClient.saveSongMetadata(metadataDto));

        assertEquals(validationErrorResponse, exception.getValidationErrorResponse());
    }

    @Test
    void saveSongMetadataShouldNotSaveDueToConflict() throws Exception {
        SongMetadataDto metadataDto = DataUtils.initSongMetaDataDto(1);
        SimpleErrorResponse validationErrorResponse = new SimpleErrorResponse(409, "Song metadata with id=123 already exists");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://song-service/songs")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(metadataDto)))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(validationErrorResponse)));

        ConflictRuntimeException exception =
                assertThrows(ConflictRuntimeException.class,
                        () -> songRestClient.saveSongMetadata(metadataDto));

        assertEquals(validationErrorResponse, exception.getErrorResponse());
    }

    @Test
    void saveSongMetadataShouldNotSaveDueServerError() throws Exception {
        SongMetadataDto metadataDto = DataUtils.initSongMetaDataDto(1);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://song-service/songs")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(metadataDto)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(null)));

        HttpClientErrorException exception =
                assertThrows(HttpClientErrorException.class,
                        () -> songRestClient.saveSongMetadata(metadataDto));

        assertEquals(500, exception.getStatusCode().value());
    }

    @Test
    void deleteSongsMetadataShouldDeleteItems() throws Exception {
        URI uri = UriComponentsBuilder.fromUriString("http://song-service/songs")
                .queryParam("id", "1,2,3")
                .build()
                .toUri();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(uri))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new Identifiables<>(List.of(1, 2)))));

        Identifiables<Integer> identifiables = songRestClient.deleteSongsMetadata(List.of(1, 2, 3));

        assertNotNull(identifiables);
        assertEquals(List.of(1, 2), identifiables.getIds());
    }

    @Test
    void deleteSongsMetadataShouldNotDeleteDataDueNotValidInput() throws Exception {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(400, Map.of("id", "Id -1 is positive int"));

        URI uri = UriComponentsBuilder.fromUriString("http://song-service/songs")
                .queryParam("id", "-1,2")
                .build()
                .toUri();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(uri))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(validationErrorResponse)));

        NotValidSongMetaDataRuntimeException exception = assertThrows(NotValidSongMetaDataRuntimeException.class,
                () -> songRestClient.deleteSongsMetadata(List.of(-1, 2)));

        assertEquals(validationErrorResponse, exception.getValidationErrorResponse());
    }
}

@Configuration
@PropertySource("application.properties")
@ComponentScan("org.example.service.client")
@Import({ApplicationConfig.class, RabbitProducerConfig.class})
class TestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }
}