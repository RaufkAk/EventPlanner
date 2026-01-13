package com.yeditepe.eventservice.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer availableSeats;
    private BigDecimal price;
}
