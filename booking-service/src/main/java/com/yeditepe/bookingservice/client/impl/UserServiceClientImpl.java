package com.yeditepe.bookingservice.client.impl;

import com.yeditepe.bookingservice.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClientImpl implements UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user.base:http://localhost:8081}")
    private String userBaseUrl;

    public UserServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Boolean validateUser(Long userId, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
        }
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Boolean> resp = restTemplate.exchange(
                userBaseUrl + "/api/users/{id}/validate",
                org.springframework.http.HttpMethod.GET,
                entity,
                Boolean.class,
                userId
        );
        return resp.getBody();
    }
}
