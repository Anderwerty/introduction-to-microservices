package org.example.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component("storage.rest.client")
public class StorageRestClientImpl implements StorageClient {
    private final String storageServiceName;
    private final RestTemplate restTemplate;

    public StorageRestClientImpl(@Value("${storage.service.name}") String storageServiceName,
                                 @Qualifier("storage.service.rest.template") RestTemplate restTemplate) {
        this.storageServiceName = storageServiceName;
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "storageService", fallbackMethod = "getStorageDetailsFallback")
    public List<StorageDetailsResponse> getStorageDetailsResponses() {
        String url = "http://" + storageServiceName + "/storages";

        ResponseEntity<List<StorageDetailsResponse>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        if (response.getBody() == null) {
            return null;
        }

        return response.getBody();
    }

    /**
     * Fallback method called by Resilience4j if circuit breaker is open or exception occurs
     */
    private List<StorageDetailsResponse> getStorageDetailsFallback(Throwable throwable) {
        return List.of(
                StorageDetailsResponse.builder()
                        .id(1)
                        .storageType(StorageType.STAGING)
                        .bucket("staging-bucket")
                        .path("/staging-files")
                        .build(),
                StorageDetailsResponse.builder()
                        .id(2)
                        .storageType(StorageType.PERMANENT)
                        .bucket("permanent-bucket")
                        .path("/permanent-files")
                        .build()
        );
    }
}
