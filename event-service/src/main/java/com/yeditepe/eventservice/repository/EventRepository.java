package com.yeditepe.eventservice.repository;

import com.yeditepe.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
