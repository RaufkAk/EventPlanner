package com.yeditepe.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yeditepe.notificationservice.event.BookingCreatedEvent;

/**
 * RabbitMQ yapılandırması
 * Queue, Exchange ve Binding'leri tanımlar
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Queue oluştur (durable = true -> sunucu restart olsa bile kalıcı)
     */
    @Bean
    public Queue notificationQueue() {
        return new Queue(queueName, true);
    }

    /**
     * Topic Exchange oluştur (routing pattern'lere göre yönlendirme)
     */
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Queue'yu Exchange'e bağla
     */
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(bookingExchange)
                .with(routingKey);
    }

    /**
     * JSON mesaj dönüştürücü (Java Object <-> JSON)
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        // Create ObjectMapper with JavaTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Create converter - without ClassMapper to avoid type-checking issues
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        // Don't set ClassMapper - Jackson will deserialize the JSON body to the method parameter type (BookingCreatedEvent)
        
        return converter;
    }

    /**
     * RabbitTemplate (mesaj gönderme/alma için)
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
