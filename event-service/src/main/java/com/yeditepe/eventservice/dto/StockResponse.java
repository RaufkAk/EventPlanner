package com.yeditepe.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private String eventId;
    private Integer availableSeats;
    private Boolean hasStock;
}


