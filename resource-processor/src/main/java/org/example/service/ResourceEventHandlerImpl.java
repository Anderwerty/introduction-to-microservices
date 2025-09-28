package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.service.client.ResourceServiceClient;
import org.example.service.client.SongServiceClient;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.SongMetadataDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ResourceEventHandlerImpl implements ResourceEventHandler {
    private final ResourceServiceClient resourceServiceClient;
    private final MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    public ResourceEventHandlerImpl(ResourceServiceClient resourceServiceClient,
                                    MetadataExtractor metadataExtractor,
                                    SongServiceClient songServiceClient) {
        this.resourceServiceClient = resourceServiceClient;
        this.metadataExtractor = metadataExtractor;
        this.songServiceClient = songServiceClient;
    }

    @Retryable(
            maxAttemptsExpression = "${retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${retry.delay}",
                    multiplierExpression = "${retry.multiplier}")
    )
    @RabbitListener(queues = "${message.resource.queue.name:}")
    @Override
    public void handle(ResourceEvent event) {
        log.debug(" Received event: {}", event);

        byte[] resourceData = resourceServiceClient.getResourceData(event.getResourceId());
        SongMetadataDto metadata = metadataExtractor.extract(resourceData);
        metadata.setId(event.getResourceId());
        log.debug("Extract metadata: {}", metadata);
        songServiceClient.saveSongMetadata(metadata);
    }

    @Recover
    public void recover(RuntimeException e, ResourceEvent event) {
        log.error("All retries failed for event {}: {}", event.getResourceId(), e.getMessage(), e);
    }
}
