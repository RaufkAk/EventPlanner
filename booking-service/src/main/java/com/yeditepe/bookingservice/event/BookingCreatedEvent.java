package com.yeditepe.bookingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent implements Serializable {
    private Long bookingId;
    private Long userId;
    private String eventId;
    private String status;
    private LocalDateTime bookingDate;
}
