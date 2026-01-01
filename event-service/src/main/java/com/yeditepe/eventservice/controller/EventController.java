package com.yeditepe.eventservice.controller;

import com.yeditepe.eventservice.dto.EventRequest;
import com.yeditepe.eventservice.dto.EventResponse;
import com.yeditepe.eventservice.dto.StockResponse;
import com.yeditepe.eventservice.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
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

    // GET /api/events/{id}/stock
    @GetMapping("/{id}/stock")
    public ResponseEntity<StockResponse> checkStock(@PathVariable String id) {
        Integer availableSeats = eventService.getAvailableSeats(id);
        boolean hasStock = eventService.checkStock(id);
        
        return ResponseEntity.ok(new StockResponse(id, availableSeats, hasStock));
    }

    // PUT /api/events/{id}/reserve
    @PutMapping("/{id}/reserve")
    public ResponseEntity<Boolean> reserveSeat(@PathVariable String id) {
        boolean reserved = eventService.reserveSeat(id);
        return ResponseEntity.ok(reserved);
    }

    // PUT /api/events/{id}/release
    @PutMapping("/{id}/release")
    public ResponseEntity<Boolean> releaseSeat(@PathVariable String id) {
        boolean released = eventService.releaseSeat(id);
        return ResponseEntity.ok(released);
    }
}
