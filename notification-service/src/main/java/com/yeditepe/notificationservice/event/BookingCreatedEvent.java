package com.yeditepe.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreatedEvent implements Serializable {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private String eventId;
    private String eventTitle;
    private Integer seatCount;
    private String status;
    private LocalDateTime bookingDate;
}
