package org.example.service;

import org.example.service.dto.ResourceEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("music.event.message.publisher")
public class MessagePublisherImpl implements MessagePublisher<ResourceEvent> {

    private final AmqpTemplate amqpTemplate;

    private final String exchange;

    private final  String routingkey;

    public MessagePublisherImpl(AmqpTemplate amqpTemplate,
                                @Value("${message.resource.topic.saved.name}") String exchange,
                                @Value("${message.resource.routing.saved.key}") String routingKey) {
        this.amqpTemplate = amqpTemplate;
        this.exchange = exchange;
        this.routingkey = routingKey;
    }

    @Override
    public void publishMessage(ResourceEvent message){

        amqpTemplate.convertAndSend(exchange, routingkey, message);
    }
}
