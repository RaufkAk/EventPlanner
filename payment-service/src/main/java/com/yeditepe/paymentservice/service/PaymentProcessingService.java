package com.yeditepe.paymentservice.service;

import com.yeditepe.paymentservice.dto.PaymentRequestDTO;
import com.yeditepe.paymentservice.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {
    
    private static final Random random = new Random();
    
    /**
     * Simulates payment processing through a payment gateway
     * In a real scenario, this would integrate with Stripe, PayPal, etc.
     */
    public boolean processPayment(Payment payment, PaymentRequestDTO paymentRequest) {
        log.info("Processing payment through gateway for transaction: {}", payment.getTransactionId());
        
        try {
            // Validate payment request
            validatePaymentRequest(paymentRequest);
            
            // Simulate payment gateway call (80% success rate for demo)
            boolean isSuccessful = random.nextDouble() < 0.80;
            
            if (isSuccessful) {
                log.info("Payment gateway approved transaction: {}", payment.getTransactionId());
                return true;
            } else {
                log.warn("Payment gateway declined transaction: {}", payment.getTransactionId());
                return false;
            }
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private void validatePaymentRequest(PaymentRequestDTO paymentRequest) {
        if (paymentRequest.getBookingId() == null || paymentRequest.getBookingId() <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        
        if (paymentRequest.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
    }
}
