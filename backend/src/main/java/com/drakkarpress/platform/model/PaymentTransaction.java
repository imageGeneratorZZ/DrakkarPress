package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad PaymentTransaction - Transacciones de pago
 * 
 * Características principales:
 * - Registro de todos los pagos (Stripe, PayPal, etc.)
 * - Estados: pending, completed, failed, refunded
 * - Tracking de membresías y renovaciones
 */
@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_transactions_user_id", columnList = "user_id"),
    @Index(name = "idx_payment_transactions_status", columnList = "payment_status"),
    @Index(name = "idx_payment_transactions_created_at", columnList = "created_at"),
    @Index(name = "idx_payment_transactions_external_id", columnList = "external_transaction_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que realizó el pago
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * ID de transacción externa (Stripe, PayPal, etc.)
     */
    @Column(name = "external_transaction_id", length = 255)
    private String externalTransactionId;

    /**
     * Proveedor de pago
     * STRIPE, PAYPAL, OXXO, etc.
     */
    @Column(name = "payment_provider", nullable = false, length = 50)
    private String paymentProvider;

    /**
     * Método de pago
     * CARD, PAYPAL, CASH, TRANSFER, etc.
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Monto pagado
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Moneda
     */
    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String currency;

    /**
     * Estado del pago
     * PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED
     */
    @Column(name = "payment_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String paymentStatus;

    /**
     * Tipo de transacción
     * MEMBERSHIP_SIGNUP, MEMBERSHIP_RENEWAL, UPGRADE, DOWNGRADE, REFUND
     */
    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    /**
     * ID de membresía relacionada
     */
    @Column(name = "membership_id")
    private UUID membershipId;

    /**
     * Plan adquirido
     */
    @Column(name = "plan_type", length = 50)
    private String planType;

    /**
     * Frecuencia de pago
     * MONTHLY, ANNUAL, LIFETIME
     */
    @Column(name = "payment_frequency", length = 20)
    private String paymentFrequency;

    /**
     * Descripción del pago
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Fecha de completación
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Fecha de fallo
     */
    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    /**
     * Razón de fallo
     */
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    /**
     * Fecha de reembolso
     */
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    /**
     * Monto reembolsado
     */
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    /**
     * Razón de reembolso
     */
    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    /**
     * Metadata adicional en JSON
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si está pendiente
     */
    public boolean isPending() {
        return "PENDING".equals(paymentStatus);
    }

    /**
     * Verifica si está completado
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(paymentStatus);
    }

    /**
     * Verifica si falló
     */
    public boolean isFailed() {
        return "FAILED".equals(paymentStatus);
    }

    /**
     * Verifica si fue reembolsado
     */
    public boolean isRefunded() {
        return "REFUNDED".equals(paymentStatus);
    }

    /**
     * Marca como completado
     */
    public void markCompleted(String externalId) {
        this.paymentStatus = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        if (externalId != null) {
            this.externalTransactionId = externalId;
        }
    }

    /**
     * Marca como fallido
     */
    public void markFailed(String reason) {
        this.paymentStatus = "FAILED";
        this.failedAt = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Procesa reembolso
     */
    public void processRefund(BigDecimal refundAmt, String reason) {
        this.paymentStatus = "REFUNDED";
        this.refundedAt = LocalDateTime.now();
        this.refundAmount = refundAmt;
        this.refundReason = reason;
    }

    /**
     * Verifica si es pago de membresía
     */
    public boolean isMembershipPayment() {
        return "MEMBERSHIP_SIGNUP".equals(transactionType) 
                || "MEMBERSHIP_RENEWAL".equals(transactionType);
    }

    /**
     * Verifica si es upgrade/downgrade
     */
    public boolean isPlanChange() {
        return "UPGRADE".equals(transactionType) || "DOWNGRADE".equals(transactionType);
    }

    /**
     * Formatea el monto
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f %s", amount, currency);
    }

    /**
     * Formatea el monto de reembolso
     */
    public String getFormattedRefundAmount() {
        if (refundAmount == null) {
            return "$0.00";
        }
        return String.format("$%.2f %s", refundAmount, currency);
    }

    /**
     * Días desde la transacción
     */
    public long getDaysSinceTransaction() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }

    /**
     * Obtiene nombre del plan
     */
    public String getPlanName() {
        if (planType == null) {
            return "Unknown";
        }
        switch (planType) {
            case "PREMIUM_PHASE_1":
                return "Fundador";
            case "PREMIUM_PHASE_2":
                return "Early Adopter";
            case "PREMIUM_PHASE_3":
                return "Premium";
            case "FREE":
                return "Gratuito";
            default:
                return planType;
        }
    }

    /**
     * Crea transacción de signup
     */
    public static PaymentTransaction createSignup(
            User user,
            String provider,
            BigDecimal amount,
            String planType,
            String frequency) {
        
        return PaymentTransaction.builder()
                .user(user)
                .paymentProvider(provider)
                .amount(amount)
                .currency("USD")
                .paymentStatus("PENDING")
                .transactionType("MEMBERSHIP_SIGNUP")
                .planType(planType)
                .paymentFrequency(frequency)
                .description("Membresía " + planType + " - " + frequency)
                .build();
    }

    /**
     * Crea transacción de renovación
     */
    public static PaymentTransaction createRenewal(
            User user,
            String provider,
            BigDecimal amount,
            UUID membershipId,
            String planType,
            String frequency) {
        
        return PaymentTransaction.builder()
                .user(user)
                .paymentProvider(provider)
                .amount(amount)
                .currency("USD")
                .paymentStatus("PENDING")
                .transactionType("MEMBERSHIP_RENEWAL")
                .membershipId(membershipId)
                .planType(planType)
                .paymentFrequency(frequency)
                .description("Renovación membresía " + planType)
                .build();
    }
}
