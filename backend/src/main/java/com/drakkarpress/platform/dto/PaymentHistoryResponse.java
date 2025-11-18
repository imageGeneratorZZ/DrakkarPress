package com.drakkarpress.platform.dto;

import com.drakkarpress.platform.model.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para historial de pagos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {

    private UUID id;
    private String externalTransactionId;
    private String paymentProvider;
    private String paymentMethod;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private String transactionType;
    private String planType;
    private String planName;
    private String paymentFrequency;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String formattedAmount;

    public static PaymentHistoryResponse from(PaymentTransaction transaction) {
        return PaymentHistoryResponse.builder()
                .id(transaction.getId())
                .externalTransactionId(transaction.getExternalTransactionId())
                .paymentProvider(transaction.getPaymentProvider())
                .paymentMethod(transaction.getPaymentMethod())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentStatus(transaction.getPaymentStatus())
                .transactionType(transaction.getTransactionType())
                .planType(transaction.getPlanType())
                .planName(transaction.getPlanName())
                .paymentFrequency(transaction.getPaymentFrequency())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .formattedAmount(transaction.getFormattedAmount())
                .build();
    }
}
