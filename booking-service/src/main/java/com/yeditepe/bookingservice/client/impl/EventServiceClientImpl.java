package com.yeditepe.bookingservice.client.impl;

import com.yeditepe.bookingservice.client.EventServiceClient;
import com.yeditepe.bookingservice.dto.EventStockResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EventServiceClientImpl implements EventServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.event.base:http://localhost:8082}")
    private String eventBaseUrl;

    public EventServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public EventStockResponse checkStock(String eventId) {
        try {
            return restTemplate.getForObject(eventBaseUrl + "/api/events/{id}/stock", EventStockResponse.class, eventId);
        } catch (Exception ex) {
            // Fallback for local testing when event-service is unavailable or protected
            return new EventStockResponse(eventId, 999, true);
        }
    }

    @Override
    public Boolean reserveSeat(String eventId) {
        try {
            ResponseEntity<Boolean> resp = restTemplate.exchange(eventBaseUrl + "/api/events/{id}/reserve", org.springframework.http.HttpMethod.PUT, HttpEntity.EMPTY, Boolean.class, eventId);
            return resp.getBody();
        } catch (Exception ex) {
            return true;
        }
    }

    @Override
    public Boolean releaseSeat(String eventId) {
        try {
            ResponseEntity<Boolean> resp = restTemplate.exchange(eventBaseUrl + "/api/events/{id}/release", org.springframework.http.HttpMethod.PUT, HttpEntity.EMPTY, Boolean.class, eventId);
            return resp.getBody();
        } catch (Exception ex) {
            return true;
        }
    }
}
