package com.yeditepe.eventservice.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String title;

    private String category;

    @Field("venue")
    private String venue;

    @Field("start_time")
    private LocalDateTime startTime;

    @Field("end_time")
    private LocalDateTime endTime;

    private Integer capacity;

    public Event() {
    }

    public Event(String id,
                 String title,
                 String category,
                 String venue,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 Integer capacity) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
