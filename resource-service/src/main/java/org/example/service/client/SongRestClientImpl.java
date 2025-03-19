package org.example.service.client;

import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;
import org.example.service.dto.SongMetadataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class SongRestClientImpl implements SongClient {
    private final String songServiceName;
    private final RestTemplate restTemplate;


    public SongRestClientImpl(RestTemplate restTemplate, @Value("${song.service.name}") String songServiceName) {
        this.restTemplate = restTemplate;
        this.songServiceName = songServiceName;
    }

    @Override
    public Identifiable<Integer> saveSongMetadata(SongMetadataDto songMetadataDto) {
        String url = "http://" + songServiceName + "/songs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SongMetadataDto> httpEntity = new HttpEntity<>(songMetadataDto, headers);
        ResponseEntity<Identifiable> responseEntity = restTemplate.postForEntity(url, httpEntity, Identifiable.class);

        return responseEntity.getBody();
    }

    @Override
    public Identifiables<Integer> deleteSongsMetadata(List<Integer> ids) {
        String url = "http://" + songServiceName + "/songs";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Integer>> httpEntity = new HttpEntity<>(headers);
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("id", String.join(",", ids.stream().map(String::valueOf).toList()))
                .toUriString();

        ResponseEntity<Identifiables> responseEntity =
                restTemplate.exchange(fullUrl, HttpMethod.DELETE, httpEntity, Identifiables.class);

        return responseEntity.getBody();
    }
}
