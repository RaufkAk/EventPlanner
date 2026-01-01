package com.yeditepe.bookingservice.client.impl;

import com.yeditepe.bookingservice.client.PaymentServiceClient;
import com.yeditepe.bookingservice.dto.PaymentRequest;
import com.yeditepe.bookingservice.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentServiceClientImpl implements PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.payment.base:http://localhost:8084}")
    private String paymentBaseUrl;

    public PaymentServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        return restTemplate.postForObject(paymentBaseUrl + "/api/payments/process", request, PaymentResponse.class);
    }
}
