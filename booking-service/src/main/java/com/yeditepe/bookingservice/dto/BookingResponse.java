package com.yeditepe.bookingservice.dto;

import com.yeditepe.bookingservice.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private String eventId;
    private BookingStatus status;
    private LocalDateTime bookingDate;
}
