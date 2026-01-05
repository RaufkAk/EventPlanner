package com.yeditepe.bookingservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_QUEUE = "notification.queue";
    public static final String BOOKING_EXCHANGE = "bookingExchange";
    public static final String BOOKING_ROUTING_KEY = "booking.created";

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE, true);
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }

    @Bean
    public Binding bookingBinding(Queue bookingQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(bookingQueue)
                .to(bookingExchange)
                .with(BOOKING_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        // Create ObjectMapper with JavaTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Create converter without ClassMapper to avoid __TypeId__ header issues
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        // Don't set ClassMapper - this prevents __TypeId__ header from being added
        // Consumer will deserialize purely based on JSON content
        
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
