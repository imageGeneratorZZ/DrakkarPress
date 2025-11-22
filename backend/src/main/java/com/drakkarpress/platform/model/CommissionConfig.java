package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "commission_config", indexes = {
        @Index(name = "idx_commission_config_context", columnList = "context"),
        @Index(name = "idx_commission_config_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Contexto de la venta:
     * DIRECT: venta directa autor->lector
     * RESELLER: venta con revendedor
     * PRINT_SHOP: venta física con imprenta (revendedor opcional)
     */
    @Column(name = "context", nullable = false, length = 30)
    private String context;

    @Column(name = "author_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal authorPercent;

    @Column(name = "reseller_percent", precision = 5, scale = 2)
    private BigDecimal resellerPercent;

    @Column(name = "print_shop_percent", precision = 5, scale = 2)
    private BigDecimal printShopPercent;

    @Column(name = "platform_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal platformPercent;

    /** Umbral de volumen para aplicar este tier */
    @Column(name = "min_volume")
    private Integer minVolume;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isCurrentlyEffective() {
        LocalDateTime now = LocalDateTime.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && now.isAfter(effectiveTo)) return false;
        return Boolean.TRUE.equals(isActive);
    }
}
