package com.yeditepe.notificationservice.service;

import com.yeditepe.notificationservice.model.BookingEvent;
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

    /**
     * Rezervasyon bildirimi işle
     * 1. Email gönder
     * 2. MongoDB'ye kaydet
     */
    public void processBookingNotification(BookingEvent event) {
        log.info("Processing notification for booking: {}", event.getBookingId());

        String subject = "Booking Confirmation - " + event.getEventTitle();

        try {
            // Email gönder
            emailService.sendBookingConfirmation(event);

            // Başarılı log kaydet
            NotificationLog successLog = NotificationLog.success(
                    Long.parseLong(event.getBookingId()),
                    event.getUserEmail(),
                    subject,
                    "Booking confirmation email sent successfully"
            );
            notificationLogRepository.save(successLog);

            log.info("Notification sent successfully to {}", event.getUserEmail());

        } catch (Exception e) {
            // Hata durumunda log kaydet
            log.error("Failed to send notification: {}", e.getMessage(), e);

            try {
                NotificationLog failureLog = NotificationLog.failure(
                        Long.parseLong(event.getBookingId()),
                        event.getUserEmail(),
                        subject,
                        e.getMessage()
                );
                notificationLogRepository.save(failureLog);
            } catch (NumberFormatException nfe) {
                log.error("Invalid bookingId format: {}", event.getBookingId());
            }

            // Exception'ı tekrar fırlat (RabbitMQ retry mekanizması için)
            throw new RuntimeException("Notification processing failed", e);
        }
    }
}