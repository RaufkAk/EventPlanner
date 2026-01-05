package com.yeditepe.notificationservice.consumer;

import com.yeditepe.notificationservice.event.BookingCreatedEvent;
import com.yeditepe.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeBookingEvent(BookingCreatedEvent event) {
        log.info("========================================");
        log.info("🔔 EMAIL GÖNDERİLİYOR: {} için", event.getUserEmail());
        log.info("Booking ID: {}", event.getBookingId());
        log.info("========================================");

        try {
            notificationService.processBookingNotification(event);
            log.info("✅ Email başarıyla gönderildi!");

        } catch (Exception e) {
            log.error("❌ Email gönderimi başarısız: {}", e.getMessage(), e);
        }
    }
}
