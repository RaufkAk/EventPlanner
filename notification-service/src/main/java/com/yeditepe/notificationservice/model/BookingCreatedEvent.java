package com.yeditepe.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Booking Service'den gelen event mesajını temsil eder
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent implements Serializable {

    private Long bookingId;
    private Long userId;
    private String userEmail;
    private String eventId;
    private String eventTitle;
    private Integer seatCount;
    private LocalDateTime bookingDate;
    private String status; // CONFIRMED, CANCELLED
}