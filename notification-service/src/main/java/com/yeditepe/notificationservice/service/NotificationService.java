package com.yeditepe.notificationservice.service;

import com.yeditepe.notificationservice.event.BookingCreatedEvent;
import com.yeditepe.notificationservice.model.NotificationLog;
import com.yeditepe.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Ana bildirim servisi
 * Email gönderimini koordine eder ve logları yönetir
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final NotificationLogRepository notificationLogRepository;

    public void processBookingNotification(BookingCreatedEvent event) {
        log.info("Processing notification for booking: {}", event.getBookingId());

        String subject = "Booking Confirmation - " + event.getEventTitle();

        try {
            emailService.sendBookingConfirmation(event);

            NotificationLog successLog = NotificationLog.success(
                    event.getBookingId(),
                    event.getUserEmail(),
                    subject,
                    "Booking confirmation email sent successfully");
            notificationLogRepository.save(successLog);

            log.info("Notification sent successfully to {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);

            try {
                NotificationLog failureLog = NotificationLog.failure(
                        event.getBookingId(),
                        event.getUserEmail(),
                        subject,
                        e.getMessage());
                notificationLogRepository.save(failureLog);
            } catch (Exception logError) {
                log.error("Failed to log notification failure: {}", logError.getMessage());
            }

            throw new RuntimeException("Notification processing failed", e);
        }
    }
}