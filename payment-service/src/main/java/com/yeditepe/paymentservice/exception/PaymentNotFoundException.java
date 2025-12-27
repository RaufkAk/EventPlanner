package com.yeditepe.paymentservice.exception;

public class PaymentNotFoundException extends PaymentException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
