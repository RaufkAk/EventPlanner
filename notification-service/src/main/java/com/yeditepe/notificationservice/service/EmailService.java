package com.yeditepe.notificationservice.service;

import com.yeditepe.notificationservice.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(BookingCreatedEvent event) {
        try {
            String subject = "Booking Confirmation - " + event.getEventTitle();
            String body = buildEmailBody(event);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getUserEmail());
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@eventplanner.com");

            mailSender.send(message);

            log.info("Email successfully sent to: {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getUserEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildEmailBody(BookingCreatedEvent event) {
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
                event.getStatus());
    }
}
