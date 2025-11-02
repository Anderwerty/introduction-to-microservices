package org.example.service;

import org.example.service.dto.ResourceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessagePublisherImplTest {

    private AmqpTemplate amqpTemplate;
    private MessagePublisherImpl messagePublisher;

    private final String exchange = "test-exchange";
    private final String routingKey = "test.key";

    @BeforeEach
    void setUp() {
        amqpTemplate = mock(AmqpTemplate.class);
        messagePublisher = new MessagePublisherImpl(amqpTemplate, exchange, routingKey);
    }

    @Test
    void testPublishMessage() {
        ResourceEvent message = new ResourceEvent(12);

        messagePublisher.publishMessage(message);

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        verify(amqpTemplate, times(1)).convertAndSend(eq(exchange), eq(routingKey), messageCaptor.capture());

        Object sentMessage = messageCaptor.getValue();
        assertEquals(message, sentMessage, "The message sent should be the same as the one passed");
    }
}
