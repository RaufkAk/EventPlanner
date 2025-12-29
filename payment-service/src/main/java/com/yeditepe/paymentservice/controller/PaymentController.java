package com.yeditepe.paymentservice.controller;

import com.yeditepe.paymentservice.dto.PaymentDTO;
import com.yeditepe.paymentservice.dto.PaymentRequestDTO;
import com.yeditepe.paymentservice.entity.PaymentStatus;
import com.yeditepe.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Process a new payment
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        log.info("Received payment request for booking: {}", paymentRequest.getBookingId());
        PaymentDTO payment = paymentService.processPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long paymentId) {
        log.info("Fetching payment: {}", paymentId);
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Get all payments for a booking
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBooking(@PathVariable Long bookingId) {
        log.info("Fetching payments for booking: {}", bookingId);
        List<PaymentDTO> payments = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Get payment by transaction ID
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransaction(@PathVariable String transactionId) {
        log.info("Fetching payment by transaction ID: {}", transactionId);
        PaymentDTO payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Get payments by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Refund a payment
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long paymentId) {
        log.info("Processing refund for payment: {}", paymentId);
        PaymentDTO payment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Get payments by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);
        List<PaymentDTO> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Get total completed payments
     */
    @GetMapping("/analytics/total-completed")
    public ResponseEntity<BigDecimal> getTotalCompletedPayments() {
        log.info("Calculating total completed payments");
        BigDecimal total = paymentService.getTotalCompletedPayments();
        return ResponseEntity.ok(total);
    }
    
    /**
     * Get total completed payments by date range
     */
    @GetMapping("/analytics/total-completed/date-range")
    public ResponseEntity<BigDecimal> getTotalCompletedPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Calculating total completed payments between {} and {}", startDate, endDate);
        BigDecimal total = paymentService.getTotalCompletedPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(total);
    }
}
