package com.yeditepe.eventservice.controller;

import com.yeditepe.eventservice.dto.EventRequest;
import com.yeditepe.eventservice.dto.EventResponse;
import com.yeditepe.eventservice.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /events?category=...&venue=...&from=...&to=...
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        List<EventResponse> events =
                eventService.getAllEvents(category, venue, from, to);
        return ResponseEntity.ok(events);
    }
//ab5566
    // GET /events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    // POST /events
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody @Validated EventRequest request) {

        EventResponse created = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String id,
            @RequestBody @Validated EventRequest request) {

        EventResponse updated = eventService.updateEvent(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
