package org.example.service.client;

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
import java.util.Objects;

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
    public StorageDetailsResponse getStorageByStorageType(StorageType storageType){
        String url = "http://" + storageServiceName + "/storages";

        ResponseEntity<List<StorageDetailsResponse>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );
        if(response.getBody() == null){
            return null;
        }

        List<StorageDetailsResponse> storages = response.getBody();
        return storages.stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getStorageType() == StorageType.STAGING)
                .findAny()
                .orElse(null);
    }
}
