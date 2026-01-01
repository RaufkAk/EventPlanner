package com.yeditepe.eventservice.repository;

import com.yeditepe.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByDateBetween(LocalDateTime from, LocalDateTime to);

    List<Event> findByTitleContainingIgnoreCase(String title);
}
