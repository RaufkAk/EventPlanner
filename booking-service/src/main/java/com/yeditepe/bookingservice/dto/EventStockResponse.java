package com.yeditepe.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStockResponse {
    private String eventId;
    private Integer availableSeats;
    private Boolean hasStock;
}
