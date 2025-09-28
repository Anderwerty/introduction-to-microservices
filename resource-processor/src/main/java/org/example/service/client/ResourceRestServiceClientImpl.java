package org.example.service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ResourceRestServiceClientImpl implements ResourceServiceClient {

    private final String resourceServiceName;
    private final RestTemplate restTemplate;

    public ResourceRestServiceClientImpl(@Qualifier("service.rest.template") RestTemplate restTemplate,
                                         @Value("${resource.service.name}") String resourceServiceName) {
        this.restTemplate = restTemplate;
        this.resourceServiceName = resourceServiceName;
    }

    @Retryable(
            maxAttemptsExpression = "${retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${retry.delay}",
                    multiplierExpression = "${retry.multiplier}")
    )
    @Override
    public byte[] getResourceData(Integer resourceId) {
        String url = "http://" + resourceServiceName + "/resources/{resourceId}";

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class, resourceId);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get resource data for id: " + resourceId);
        }
    }
}
