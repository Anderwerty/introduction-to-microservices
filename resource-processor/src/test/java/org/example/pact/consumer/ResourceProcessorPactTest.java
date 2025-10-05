package org.example.pact.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.example.service.dto.SongMetadataDto;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "SongService", port = "8081")
class ResourceProcessorPactTest {

    @Pact(consumer = "ResourceProcessor")
    public V4Pact getSongMetaDataPact(PactBuilder builder) {
        return builder.expectsToReceiveHttpInteraction("A request for song metadata", http ->
                        http
                                .withRequest(req -> req
                                        .method("GET")
                                        .path("/songs/123")
                                )
                                .willRespondWith(resp -> resp
                                        .status(200)
                                        .headers(Map.of("Content-Type", "application/json"))
                                        .body(LambdaDsl.newJsonBody(o -> o
                                                .numberType("id", 123)
                                                .stringType("name", "Imagine")
                                                .stringType("artist", "John Lennon")
                                                .stringType("album", "Imagine")
                                                .stringMatcher("duration", "^(0[0-9]|[1-5][0-9]):[0-5][0-9]$", "03:03")
                                                .stringMatcher("year", "^(19[0-9]{2}|20[0-9]{2})$", "1971")
                                        ).build())
                                )
                )
                .toPact();
    }

    @Test
    void testGetSongMetaData(MockServer mockServer) {
        String url = mockServer.getUrl() + "/songs/123";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SongMetadataDto> response = restTemplate.getForEntity(url, SongMetadataDto.class);
        SongMetadataDto dto = response.getBody();

        assertAll("SongMetadataDto checks",
                () -> assertThat(dto.getId(), is(123)),
                () -> assertThat(dto.getName(), is("Imagine")),
                () -> assertThat(dto.getArtist(), is("John Lennon")),
                () -> assertThat(dto.getAlbum(), is("Imagine")),
                () -> assertThat(dto.getDuration(), is("03:03")),
                () -> assertThat(dto.getYear(), is("1971"))
        );
    }
}
