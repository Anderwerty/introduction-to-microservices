package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitProducerConfig {

    private final String exchange;
    private final String queueName;
    private final String routingKey;

    private final String savedExchange;
    private final String savedQueueName;
    private final String savedRoutingKey;

    public RabbitProducerConfig(@Value("${message.resource.topic.name}") String exchange,
                                @Value("${message.resource.queue.name}") String queueName,
                                @Value("${message.resource.routing.key}") String routingKey,
                                @Value("${message.resource.topic.saved.name}")String savedExchange,
                                @Value("${message.resource.queue.saved.name}")String savedQueueName,
                                @Value("${message.resource.routing.saved.key}")String savedRoutingKey) {
        this.exchange = exchange;
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.savedExchange = savedExchange;
        this.savedQueueName = savedQueueName;
        this.savedRoutingKey = savedRoutingKey;
    }
    @Bean("stagingResourceQueue")
    public Queue resourceQueue() {
        return new Queue(queueName, true);
    }

    @Bean("stagingResourceExchange")
    public DirectExchange resourceExchange() {
        return new DirectExchange(exchange);
    }

    @Bean("stagingBinding")
    public Binding binding(@Qualifier("stagingResourceQueue") Queue resourceQueue,
                           @Qualifier("stagingResourceExchange") DirectExchange resourceExchange) {
        return BindingBuilder.bind(resourceQueue).to(resourceExchange).with(routingKey);
    }

    @Bean("savedResourceQueue")
    public Queue savedResourceQueue() {
        return new Queue(savedQueueName, true);
    }

    @Bean("saveResourceExchange")
    public DirectExchange saveResourceExchange() {
        return new DirectExchange(savedExchange);
    }

    @Bean("savedBinding")
    public Binding savedBinding(@Qualifier("savedResourceQueue") Queue resourceQueue,
                                @Qualifier("saveResourceExchange") DirectExchange resourceExchange) {
        return BindingBuilder.bind(resourceQueue).to(resourceExchange).with(savedRoutingKey);
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

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}

