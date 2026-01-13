package com.yeditepe.eventservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String location;

    @NotNull
    private LocalDateTime date;

    @NotNull
    @Min(1)
    private Integer availableSeats;

    @NotNull
    @Min(0)
    private BigDecimal price;
}
