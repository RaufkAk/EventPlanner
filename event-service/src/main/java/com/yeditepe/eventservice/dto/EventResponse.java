package com.yeditepe.eventservice.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class EventResponse {

    private String id;
    private String title;
    private LocalDateTime date;
    private Integer availableSeats;
    private BigDecimal price;

    public EventResponse() {
    }

    public EventResponse(String id, String title, LocalDateTime date, Integer availableSeats, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
