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
public class BookingEvent implements Serializable {

    private String bookingId;
    private String userId;
    private String userEmail;
    private String eventId;
    private String eventTitle;
    private Integer seatCount;
    private LocalDateTime bookingDate;
    private String status; // CONFIRMED, CANCELLED

    // Constructor for quick creation
    public BookingEvent(String bookingId, String userEmail, String eventTitle) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.eventTitle = eventTitle;
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
}