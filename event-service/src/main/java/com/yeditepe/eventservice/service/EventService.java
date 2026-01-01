package com.yeditepe.eventservice.service;

import com.yeditepe.eventservice.dto.EventRequest;
import com.yeditepe.eventservice.dto.EventResponse;
import com.yeditepe.eventservice.model.Event;
import com.yeditepe.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getAllEvents(String category,
                                            String venue,
                                            LocalDateTime from,
                                            LocalDateTime to) {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(e -> filterByCategory(e, category))
                .filter(e -> filterByVenue(e, venue))
                .filter(e -> filterByDateRange(e, from, to))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return toResponse(event);
    }

    public EventResponse createEvent(EventRequest request) {
        validateDates(request.getStartTime(), request.getEndTime());

        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setTitle(request.getTitle());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setCapacity(request.getCapacity());
        event.setAvailableSeats(request.getCapacity()); // Initialize available seats to capacity

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public EventResponse updateEvent(String id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        validateDates(request.getStartTime(), request.getEndTime());

        event.setTitle(request.getTitle());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setCapacity(request.getCapacity());

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    // Stock management methods
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
        
        if (event.getAvailableSeats() >= event.getCapacity()) {
            return false; // Already at max capacity
        }
        
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);
        return true;
    }

    // ---- helpers ----

    private boolean filterByCategory(Event e, String category) {
        if (!StringUtils.hasText(category)) {
            return true;
        }
        return category.equalsIgnoreCase(e.getCategory());
    }

    private boolean filterByVenue(Event e, String venue) {
        if (!StringUtils.hasText(venue)) {
            return true;
        }
        return e.getVenue() != null &&
                e.getVenue().toLowerCase().contains(venue.toLowerCase());
    }

    private boolean filterByDateRange(Event e, LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return true;
        }
        LocalDateTime start = e.getStartTime();
        if (start == null) return false;

        boolean afterFrom = (from == null) || !start.isBefore(from);
        boolean beforeTo = (to == null) || !start.isAfter(to);
        return afterFrom && beforeTo;
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getCategory(),
                e.getVenue(),
                e.getStartTime(),
                e.getEndTime(),
                e.getCapacity()
        );
    }
}
