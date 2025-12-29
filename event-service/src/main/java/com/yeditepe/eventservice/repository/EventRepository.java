package com.yeditepe.eventservice.repository;

import com.yeditepe.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByVenueContainingIgnoreCase(String venue);

    List<Event> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}
