package com.yeditepe.paymentservice.service;

import com.yeditepe.paymentservice.dto.PaymentDTO;
import com.yeditepe.paymentservice.dto.PaymentRequestDTO;
import com.yeditepe.paymentservice.entity.Payment;
import com.yeditepe.paymentservice.entity.PaymentStatus;
import com.yeditepe.paymentservice.exception.PaymentNotFoundException;
import com.yeditepe.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentProcessingService paymentProcessingService;
    
    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO paymentRequest) {
        log.info("Processing payment for booking: {}", paymentRequest.getBookingId());
        
        if (paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        
        Payment payment = Payment.builder()
            .bookingId(paymentRequest.getBookingId())
            .amount(paymentRequest.getAmount())
            .paymentMethod(paymentRequest.getPaymentMethod())
            .status(PaymentStatus.PENDING)
            .transactionId(generateTransactionId())
            .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Payment created with ID: {}", savedPayment.getId());
        
        // Process payment through payment gateway
        boolean isProcessed = paymentProcessingService.processPayment(savedPayment, paymentRequest);
        
        if (isProcessed) {
            savedPayment.setStatus(PaymentStatus.COMPLETED);
            savedPayment.setPaymentDate(LocalDateTime.now());
            log.info("Payment processed successfully. Transaction ID: {}", savedPayment.getTransactionId());
        } else {
            savedPayment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment processing failed. Transaction ID: {}", savedPayment.getTransactionId());
        }
        
        Payment updatedPayment = paymentRepository.save(savedPayment);
        return mapToDTO(updatedPayment);
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        log.debug("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
        return mapToDTO(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByBookingId(Long bookingId) {
        log.debug("Fetching payments for booking: {}", bookingId);
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return payments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        log.debug("Fetching payment with transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction ID: " + transactionId));
        return mapToDTO(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        log.debug("Fetching payments with status: {}", status);
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        log.info("Refunding payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        
        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully. ID: {}", paymentId);
        return mapToDTO(refundedPayment);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalCompletedPayments() {
        log.debug("Calculating total completed payments");
        return paymentRepository.getTotalCompletedPayments();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalCompletedPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating total completed payments between {} and {}", startDate, endDate);
        return paymentRepository.getTotalCompletedPaymentsByDateRange(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching payments between {} and {}", startDate, endDate);
        List<Payment> payments = paymentRepository.findPaymentsByDateRange(startDate, endDate);
        return payments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
    
    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
            .id(payment.getId())
            .bookingId(payment.getBookingId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .paymentMethod(payment.getPaymentMethod())
            .transactionId(payment.getTransactionId())
            .paymentDate(payment.getPaymentDate())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
}
