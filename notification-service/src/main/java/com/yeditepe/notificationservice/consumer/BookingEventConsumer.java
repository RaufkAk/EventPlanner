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
        log.info("Received booking event for user: {}", event.getUserEmail());

        try {
            notificationService.processBookingNotification(event);
            log.info("Notification processed for booking: {}", event.getBookingId());

        } catch (Exception e) {
            log.error("Notification processing failed: {}", e.getMessage());
        }
    }
}
