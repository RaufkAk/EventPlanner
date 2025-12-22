package com.yeditepe.notificationservice.consumer;

import com.yeditepe.notificationservice.model.BookingEvent;
import com.yeditepe.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ'dan gelen rezervasyon mesajlarını dinler
 * Bu class otomatik olarak queue'dan mesaj okur ve işler
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventConsumer {

    private final NotificationService notificationService;


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeBookingEvent(BookingEvent event) {
        log.info("========================================");
        log.info("Received booking event from RabbitMQ");
        log.info("Booking ID: {}", event.getBookingId());
        log.info("User Email: {}", event.getUserEmail());
        log.info("Event Title: {}", event.getEventTitle());
        log.info("========================================");

        try {
            // Bildirimi işle
            notificationService.processBookingNotification(event);
            log.info("✅ Notification processed successfully");

        } catch (Exception e) {
            log.error("❌ Failed to process notification: {}", e.getMessage(), e);
        }
    }
}
