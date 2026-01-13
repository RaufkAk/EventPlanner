package com.yeditepe.bookingservice.client;

import com.yeditepe.bookingservice.dto.EventStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "event-service")
public interface EventServiceClient {
    @GetMapping("/api/events/{id}/stock")
    EventStockResponse checkStock(@PathVariable("id") String eventId);

    @PutMapping("/api/events/{id}/reserve")
    Boolean reserveSeat(@PathVariable("id") String eventId, @RequestHeader("Authorization") String token);

    @PutMapping("/api/events/{id}/release")
    Boolean releaseSeat(@PathVariable("id") String eventId, @RequestHeader("Authorization") String token);
}
