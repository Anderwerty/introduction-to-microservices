package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitProducerConfig {

    private final String exchange;
    private final String queueName;

    private final String routingKey;

    public RabbitProducerConfig(@Value("${message.resource.topic.name}") String exchange,
                                @Value("${message.resource.queue.name}") String queueName,
                                @Value("${message.resource.routing.key}") String routingKey) {
        this.exchange = exchange;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }
    @Bean
    public Queue resourceQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange resourceExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding binding(Queue resourceQueue, DirectExchange resourceExchange) {
        return BindingBuilder.bind(resourceQueue).to(resourceExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

}

