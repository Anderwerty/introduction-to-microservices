package org.example.service.client;

import io.opentelemetry.api.trace.Span;
import org.example.service.dto.ResourceEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("music.event.message.publisher")
public class MessagePublisherImpl implements MessagePublisher<ResourceEvent> {

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
    public void publishMessage(ResourceEvent message){
        String traceId = Span.current().getSpanContext().getTraceId();

        MessagePostProcessor processor = msg -> {
            msg.getMessageProperties().setHeader("trace_id", traceId);
            return msg;
        };

        amqpTemplate.convertAndSend(exchange, routingkey, message, processor);
    }

}
