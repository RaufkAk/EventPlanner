package com.yeditepe.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB'de saklanacak bildirim kayıtları
 */
@Document(collection = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    private String id;

    private String bookingId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String status; // SENT, FAILED
    private LocalDateTime sentAt;
    private String errorMessage;

    // Factory method for successful notification
    public static NotificationLog success(String bookingId, String email, String subject, String message) {
        return NotificationLog.builder()
                .bookingId(bookingId)
                .recipientEmail(email)
                .subject(subject)
                .message(message)
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();
    }

    // Factory method for failed notification
    public static NotificationLog failure(String bookingId, String email, String subject, String error) {
        return NotificationLog.builder()
                .bookingId(bookingId)
                .recipientEmail(email)
                .subject(subject)
                .status("FAILED")
                .sentAt(LocalDateTime.now())
                .errorMessage(error)
                .build();
    }
}
