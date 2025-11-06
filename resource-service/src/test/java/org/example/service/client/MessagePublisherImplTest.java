package org.example.service.client;

import org.example.service.dto.ResourceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagePublisherImplTest {

    private static final String EXCHANGE = "music";
    private static final String RESOURCE_MUSIC = "resource.music";
    private static final Integer SONG_ID = 123;

    @Mock
    private RabbitTemplate rabbitTemplate;
    private MessagePublisherImpl messageClient;

    @BeforeEach
    void setUp() {
        messageClient = new MessagePublisherImpl(rabbitTemplate, EXCHANGE, RESOURCE_MUSIC);
    }

    @Test
    void testSendMessage() {
        ResourceEvent message = new ResourceEvent(SONG_ID);

        messageClient.publishMessage(message);

        ArgumentCaptor<ResourceEvent> captor = ArgumentCaptor.forClass(ResourceEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(EXCHANGE), eq(RESOURCE_MUSIC), captor.capture(), any(MessagePostProcessor.class));

        ResourceEvent capturedMessage = captor.getValue();
        assertEquals(SONG_ID, capturedMessage.getResourceId());
    }
}
