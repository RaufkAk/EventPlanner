package com.yeditepe.eventservice.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "events")
public class Event {

    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Column(name = "price")
    private BigDecimal price;

    public Event() {
    }

    public Event(String id, String title, LocalDateTime date, Integer availableSeats, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Getters & Setters

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
