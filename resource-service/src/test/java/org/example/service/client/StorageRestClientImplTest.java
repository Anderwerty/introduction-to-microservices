package org.example.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StorageRestClientTest.TestConfig.class)
class StorageRestClientTest {

    @Autowired
    private StorageRestClientImpl storageRestClient;

    @Autowired
    @Qualifier("storage.service.rest.template")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getStorageByStorageTypeShouldReturnMatchingStorage() throws Exception {
        List<StorageDetailsResponse> responseList = List.of(
                new StorageDetailsResponse(1, StorageType.STAGING, "bucket1", "/path1"),
                new StorageDetailsResponse(2, StorageType.PERMANENT, "bucket2", "/path2")
        );

        mockServer.expect(ExpectedCount.once(), requestTo("http://storage-service/storages"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(responseList)));

        StorageDetailsResponse result = storageRestClient.getStorageByStorageType(StorageType.STAGING);

        assertAll(
                () -> assertThat(result, is(notNullValue())),
                () -> assertThat(result.getStorageType(), is(StorageType.STAGING)),
                () -> assertThat(result.getBucket(), is("bucket1")));
    }

    @Test
    void getStorageByStorageTypeShouldReturnNullWhenNoMatchingStorage() throws Exception {
        List<StorageDetailsResponse> responseList = List.of(
                new StorageDetailsResponse(1, StorageType.PERMANENT, "bucket2", "/path2")
        );

        mockServer.expect(ExpectedCount.once(), requestTo("http://storage-service/storages"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(responseList)));

        StorageDetailsResponse result = storageRestClient.getStorageByStorageType(StorageType.STAGING);
        assertThat(result, is(nullValue()));
    }

    @Test
    void getStorageBySto3rageTypeShouldReturnNullWhenResponseBodyIsNull() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://storage-service/storages"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("null"));

        StorageDetailsResponse result = storageRestClient.getStorageByStorageType(StorageType.STAGING);
        assertThat(result, is(nullValue()));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        @Qualifier("storage.service.rest.template")
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public StorageRestClientImpl storageRestClient(@Qualifier("storage.service.rest.template") RestTemplate restTemplate) {
            return new StorageRestClientImpl("storage-service", restTemplate);
        }
    }
}
