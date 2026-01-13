package com.yeditepe.eventservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Event {

    @Id
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private Integer availableSeats;
    private BigDecimal price;
}
