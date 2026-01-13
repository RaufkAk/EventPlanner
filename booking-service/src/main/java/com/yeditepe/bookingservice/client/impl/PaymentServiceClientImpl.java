package com.yeditepe.bookingservice.client.impl;

import com.yeditepe.bookingservice.client.PaymentServiceClient;
import com.yeditepe.bookingservice.dto.PaymentRequest;
import com.yeditepe.bookingservice.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentServiceClientImpl implements PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.payment.url:http://localhost:8084}")
    private String paymentBaseUrl;

    public PaymentServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request, String token) {
        HttpHeaders headers = new HttpHeaders();
        // Token comes as "Bearer <token>" usually. If token is just raw token, we add
        // Bearer.
        // But user passes "authorizationHeader" which is usually "Bearer ...".
        // Let's assume it's the full header value.
        headers.set("Authorization", token);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(paymentBaseUrl + "/api/payments/process", entity, PaymentResponse.class);
    }
}
