package com.yeditepe.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import com.yeditepe.paymentservice.entity.PaymentMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String cardToken;
    private String bankAccountNumber;
}
