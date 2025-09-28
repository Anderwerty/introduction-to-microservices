package org.example.service.client;

import org.example.service.dto.Identifiable;
import org.example.service.dto.SongMetadataDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SongRestServiceClientImpl implements SongServiceClient {
    private final String songServiceName;
    private final RestTemplate restTemplate;


    public SongRestServiceClientImpl(@Qualifier("service.rest.template") RestTemplate restTemplate,
                                     @Value("${song.service.name}") String songServiceName) {
        this.restTemplate = restTemplate;
        this.songServiceName = songServiceName;
    }

    @Retryable(
            maxAttemptsExpression = "${retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${retry.delay}",
                    multiplierExpression = "${retry.multiplier}")
    )
    @Override
    public Identifiable<Integer> saveSongMetadata(SongMetadataDto songMetadataDto) {
        String url = "http://" + songServiceName + "/songs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SongMetadataDto> httpEntity = new HttpEntity<>(songMetadataDto, headers);
        ResponseEntity<Identifiable> responseEntity = restTemplate.postForEntity(url, httpEntity, Identifiable.class);

        return responseEntity.getBody();
    }
}
