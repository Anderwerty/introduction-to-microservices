package org.example.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SongMetaDataSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private SongMetaDataDto songMetaData;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the application is running")
    public void theApplicationIsRunning() {
        assertThat(restTemplate).isNotNull();
    }

    @Given("I have valid song metadata")
    public void iHaveValidSongMetadata() {
        songMetaData = SongMetaDataDto.builder()
                .id(101)
                .name("Yesterday")
                .artist("The Beatles")
                .album("Help!")
                .duration("03:15")
                .year("1965")
                .build();
    }

    @Given("I have song metadata with invalid duration")
    public void iHaveSongMetadataWithInvalidDuration() {
        songMetaData = SongMetaDataDto.builder()
                .id(2)
                .name("Invalid Song")
                .artist("Bad Artist")
                .album("No Album")
                .duration("5:61")
                .year("2020")
                .build();
    }

    @Given("I have song metadata with missing song name")
    public void iHaveSongMetadataWithMissingSongName() {
        songMetaData = SongMetaDataDto.builder()
                .id(3)
                .artist("Unknown")
                .album("Unknown")
                .duration("02:59")
                .year("2019")
                .build();
    }

    @Given("I have multiple existing songs")
    public void iHaveMultipleExistingSongs() {
        SongMetaDataDto song1 = SongMetaDataDto.builder()
                .id(1)
                .name("Song One")
                .artist("Artist One")
                .album("Album One")
                .duration("04:20")
                .year("2010")
                .build();

        SongMetaDataDto song2 = SongMetaDataDto.builder()
                .id(2)
                .name("Song Two")
                .artist("Artist Two")
                .album("Album Two")
                .duration("03:45")
                .year("2012")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity("/songs", new HttpEntity<>(song1, headers), String.class);
        restTemplate.postForEntity("/songs", new HttpEntity<>(song2, headers), String.class);
    }

    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        response = restTemplate.postForEntity(endpoint, new HttpEntity<>(songMetaData, headers), String.class);
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = restTemplate.getForEntity(endpoint, String.class);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = restTemplate.exchange(endpoint, HttpMethod.DELETE, null, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(Integer expectedStatus) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @Then("the response should contain a valid ID")
    public void theResponseShouldContainAValidId() {
        assertThat(response.getBody()).contains("id");
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String expectedText) {
        assertThat(response.getBody()).contains(expectedText);
    }

    @Then("the response should contain json:")
    public void theResponseShouldContainJson(String expectedJson) throws Exception {
        JsonNode expectedNode = objectMapper.readTree(expectedJson);
        JsonNode actualNode = objectMapper.readTree(response.getBody());

        assertThat(actualNode).isEqualTo(expectedNode);
    }
}
