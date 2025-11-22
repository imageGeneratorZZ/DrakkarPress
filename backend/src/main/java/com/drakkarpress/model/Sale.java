package com.drakkarpress.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sales")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reseller_id")
    private User reseller; // NULL si es venta directa

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType type; // DIGITAL o PHYSICAL

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Precio total

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal commissionAuthor;

    @Column(precision = 10, scale = 2)
    private BigDecimal commissionReseller; // NULL si venta directa

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal commissionPlatform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(length = 255)
    private String paymentId; // ID de Stripe/PayPal

    @Column(length = 50)
    private String paymentMethod; // STRIPE, PAYPAL, OXXO

    @Column(length = 100)
    private String transactionId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDirect = true; // true = venta directa, false = con revendedor

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime saleDate;

    @Column
    private LocalDateTime paidAt;

    // Para ventas físicas
    @Column(length = 255)
    private String shippingAddress;

    @Column(length = 100)
    private String shippingCity;

    @Column(length = 100)
    private String shippingCountry;

    @Column(length = 20)
    private String shippingPostalCode;

    @Column(length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;

    // Metadata
    @Column(length = 500)
    private String notes;

    public enum SaleType {
        DIGITAL,
        PHYSICAL
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }

    public enum ShippingStatus {
        PENDING,
        PROCESSING,
        PRINTED,
        SHIPPED,
        IN_TRANSIT,
        DELIVERED,
        RETURNED
    }

    /**
     * Calcula las comisiones según el modelo de negocio:
     * - Venta directa: 90% autor, 10% plataforma
     * - Con revendedor: 60% autor, 30% revendedor, 10% plataforma
     */
    public void calculateCommissions() {
        if (amount == null) return; // salvaguarda
        if (isDirect) {
            this.commissionAuthor = amount.multiply(new BigDecimal("0.90"));
            this.commissionReseller = BigDecimal.ZERO;
            this.commissionPlatform = amount.multiply(new BigDecimal("0.10"));
        } else {
            this.commissionAuthor = amount.multiply(new BigDecimal("0.60"));
            this.commissionReseller = amount.multiply(new BigDecimal("0.30"));
            this.commissionPlatform = amount.multiply(new BigDecimal("0.10"));
        }
    }
}
