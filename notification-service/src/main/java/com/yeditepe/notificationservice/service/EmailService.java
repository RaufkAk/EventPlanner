package com.yeditepe.notificationservice.service;

import com.yeditepe.notificationservice.model.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Email gönderme servisi
 * NOT: Gerçek email göndermek için Gmail App Password gerekir
 * Şimdilik simülasyon yapıyoruz (log'a yazıyoruz)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /**
     * Rezervasyon onay emaili gönder
     */
    public void sendBookingConfirmation(BookingEvent event) {
        try {
            String subject = "Booking Confirmation - " + event.getEventTitle();
            String body = buildEmailBody(event);

            // SIMULATED: Gerçek email göndermek yerine log'a yazıyoruz
            log.info("=== EMAIL SENT ===");
            log.info("To: {}", event.getUserEmail());
            log.info("Subject: {}", subject);
            log.info("Body:\n{}", body);
            log.info("==================");

            // REAL IMPLEMENTATION (uncommment when ready):
            // SimpleMailMessage message = new SimpleMailMessage();
            // message.setTo(event.getUserEmail());
            // message.setSubject(subject);
            // message.setText(body);
            // message.setFrom("noreply@eventplanner.com");
            // mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getUserEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Email içeriği oluştur
     */
    private String buildEmailBody(BookingEvent event) {
        return String.format("""
                Dear Customer,
                
                Your booking has been confirmed!
                
                Booking Details:
                ================
                Booking ID: %s
                Event: %s
                Number of Seats: %d
                Booking Date: %s
                Status: %s
                
                Thank you for using EventPlanner!
                
                Best regards,
                EventPlanner Team
                """,
                event.getBookingId(),
                event.getEventTitle(),
                event.getSeatCount() != null ? event.getSeatCount() : 1,
                event.getBookingDate(),
                event.getStatus()
        );
    }
}
