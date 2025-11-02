package org.example.service.client;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.entity.FileState;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.dto.ResourceEvent;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
@AllArgsConstructor
public class ResourcePermanentEventHandlerImpl implements ResourceEventHandler {

    private final ResourceRepository resourceRepository;

    @RabbitListener(queues = "${message.resource.queue.saved.name:}")
    @Override
    @Transactional
    public void handle(ResourceEvent event) {
        log.debug(" Received event: {}", event);

        Resource resource = resourceRepository.findById(event.getResourceId())
                .orElseThrow(ResourceNotFoundException::new);

        resource.setFileState(FileState.PERMANENT);
        resourceRepository.save(resource);
    }

}
