package com.mart_clone.payment_service.service;

import com.mart_clone.payment_service.dto.PaymentDTO;
import com.mart_clone.payment_service.dto.ProcessPaymentRequest;
import com.mart_clone.payment_service.model.Payment;
import com.mart_clone.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public PaymentDTO processPayment(ProcessPaymentRequest request) {
        // Simulate payment processing
        String transactionId = UUID.randomUUID().toString();
        
        Payment payment = Payment.builder()
            .orderId(request.getOrderId())
            .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
            .paymentStatus(Payment.PaymentStatus.PROCESSING)
            .amount(request.getAmount())
            .transactionId(transactionId)
            .build();
        
        payment = paymentRepository.save(payment);
        
        // Simulate payment gateway call
        boolean paymentSuccess = simulatePaymentGateway(request);
        
        if (paymentSuccess) {
            payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        }
        
        payment = paymentRepository.save(payment);
        
        log.info("Processed payment {} for order {}", payment.getId(), payment.getOrderId());
        
        return convertToDTO(payment);
    }
    
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    private boolean simulatePaymentGateway(ProcessPaymentRequest request) {
        // Simulate payment processing - always success for now
        return true;
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .paymentMethod(payment.getPaymentMethod().name())
            .paymentStatus(payment.getPaymentStatus().name())
            .transactionId(payment.getTransactionId())
            .amount(payment.getAmount())
            .build();
    }
}