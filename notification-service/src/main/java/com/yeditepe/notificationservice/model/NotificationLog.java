package com.yeditepe.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    private String id;

    private Long bookingId;

    private String recipient;

    private String message;

    private String status;

    public static NotificationLog success(Long bookingId, String recipient, String subject, String message) {
        return NotificationLog.builder()
                .bookingId(bookingId)
                .recipient(recipient)
                .message(subject + " - " + message)
                .status("SUCCESS")
                .build();
    }

    public static NotificationLog failure(Long bookingId, String recipient, String subject, String message) {
        return NotificationLog.builder()
                .bookingId(bookingId)
                .recipient(recipient)
                .message(subject + " - " + message)
                .status("FAILED")
                .build();
    }
}
