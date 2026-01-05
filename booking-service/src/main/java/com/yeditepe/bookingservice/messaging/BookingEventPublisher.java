package com.yeditepe.bookingservice.messaging;

import com.yeditepe.bookingservice.config.RabbitMQConfig;
import com.yeditepe.bookingservice.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingCreatedEvent(BookingCreatedEvent event) {
        log.info("🚀 Publishing booking created event to RabbitMQ: {}", event);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.BOOKING_EXCHANGE, RabbitMQConfig.BOOKING_ROUTING_KEY, event);
            log.info("✅ Booking created event published successfully to {}/{}", RabbitMQConfig.BOOKING_EXCHANGE, RabbitMQConfig.BOOKING_ROUTING_KEY);
        } catch (Exception e) {
            log.error("❌ Failed to publish: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
