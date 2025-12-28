package com.mart_clone.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String cardNumber;
    private String cardName;
    private String expiryDate;
    private String cvv;
}