package org.example.service.client;

import org.example.service.dto.Identifiable;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisherImpl implements MessagePublisher<Identifiable<Integer>> {

    private final AmqpTemplate amqpTemplate;


    private final String exchange;

    private final  String routingkey;

    public MessagePublisherImpl(AmqpTemplate amqpTemplate,
                                @Value("${message.resource.topic.name}") String exchange,
                                @Value("${message.resource.routing.key}") String routingKey) {
        this.amqpTemplate = amqpTemplate;
        this.exchange = exchange;
        this.routingkey = routingKey;
    }

    @Override
    public void publishMessage(Identifiable<Integer> message){
        amqpTemplate.convertAndSend(exchange,routingkey,message);
    }
}
