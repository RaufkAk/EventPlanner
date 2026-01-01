package com.yeditepe.eventservice.service;

import com.yeditepe.eventservice.dto.EventRequest;
import com.yeditepe.eventservice.dto.EventResponse;
import com.yeditepe.eventservice.model.Event;
import com.yeditepe.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return toResponse(event);
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setTitle(request.getTitle());
        event.setDate(request.getDate());
        event.setAvailableSeats(request.getAvailableSeats());
        event.setPrice(request.getPrice());

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public EventResponse updateEvent(String id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setTitle(request.getTitle());
        event.setDate(request.getDate());
        event.setAvailableSeats(request.getAvailableSeats());
        event.setPrice(request.getPrice());

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    public boolean checkStock(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return event.getAvailableSeats() != null && event.getAvailableSeats() > 0;
    }

    public Integer getAvailableSeats(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return event.getAvailableSeats() != null ? event.getAvailableSeats() : 0;
    }

    public boolean reserveSeat(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        
        if (event.getAvailableSeats() == null || event.getAvailableSeats() <= 0) {
            return false;
        }
        
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);
        return true;
    }

    public boolean releaseSeat(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        
        if (event.getAvailableSeats() == null) {
            event.setAvailableSeats(0);
        }
        
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);
        return true;
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getDate(),
                e.getAvailableSeats(),
                e.getPrice()
        );
    }
}
