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
 * Entidad Membership - Membresía del usuario
 * 
 * Características principales:
 * - Planes por fase: PHASE_1 ($5), PHASE_2 ($10), PHASE_3 ($19.99)
 * - Grandfathering: usuarios de fases tempranas mantienen precio de por vida
 * - Courtesy access: admin puede otorgar premium gratis
 * - Frecuencia: mensual, anual o lifetime
 */
@Entity
@Table(name = "memberships", indexes = {
    @Index(name = "idx_memberships_user_id", columnList = "user_id", unique = true),
    @Index(name = "idx_memberships_plan", columnList = "plan"),
    @Index(name = "idx_memberships_status", columnList = "status"),
    @Index(name = "idx_memberships_is_grandfathered", columnList = "is_grandfathered")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario dueño de esta membresía (relación 1:1)
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Plan de membresía actual
     * FREE: acceso gratuito limitado
     * PREMIUM_PHASE_1: $5/mes ($50/año) - Fundadores (1-1,000) GRANDFATHERED
     * PREMIUM_PHASE_2: $10/mes ($100/año) - Early Adopters (1,001-10,000) GRANDFATHERED
     * PREMIUM_PHASE_3: $15/mes ($150/año) - Launch Promo (10,001-15,000) GRANDFATHERED
     * PREMIUM_REGULAR: $19.90/mes ($170/año) - Precio regular (15,001+)
     * PREMIUM_COURTESY: Gratis - Otorgado por admin
     */
    @Column(name = "plan", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'FREE'")
    private String plan;

    /**
     * Estado de la membresía
     * ACTIVE, EXPIRED, CANCELLED, SUSPENDED
     */
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status;

    /**
     * Frecuencia de pago
     * MONTHLY, ANNUAL, LIFETIME
     */
    @Column(name = "payment_frequency", length = 20)
    private String paymentFrequency;

    /**
     * Precio mensual pagado por el usuario
     * Guardado para mantener precio grandfathered
     */
    @Column(name = "price_paid", precision = 10, scale = 2)
    private BigDecimal pricePaid;

    /**
     * Flag de grandfathering
     * Si true, el usuario mantiene su precio de por vida
     * Usuarios de PHASE_1 y PHASE_2 tienen este flag en true
     */
    @Column(name = "is_grandfathered", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isGrandfathered;

    /**
     * Si es cortesía (otorgada por admin)
     * Estos usuarios tienen acceso premium sin pagar
     */
    @Column(name = "is_courtesy", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isCourtesy;

    /**
     * Razón de cortesía (si aplica)
     * Ej: "Invitado especial", "Staff", "Beta tester", etc.
     */
    @Column(name = "courtesy_reason", columnDefinition = "TEXT")
    private String courtesyReason;

    /**
     * Admin que otorgó la cortesía (si aplica)
     */
    @Column(name = "courtesy_granted_by")
    private UUID courtesyGrantedBy;

    /**
     * Fecha de inicio de la membresía
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * Fecha de expiración de la membresía
     * NULL para lifetime o cortesía permanente
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Fecha de cancelación (si aplica)
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * Si la membresía está activa
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

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
     * Verifica si la membresía está vencida
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false; // Lifetime o cortesía sin expiración
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Verifica si es membresía premium (cualquier tipo)
     */
    public boolean isPremium() {
        return plan != null && !plan.equals("FREE");
    }

    /**
     * Verifica si puede usar IA ilimitada
     */
    public boolean hasUnlimitedAI() {
        return isPremium() && isActive && !isExpired();
    }

    /**
     * Calcula el precio según la fase del usuario
     */
    public static BigDecimal calculatePriceByPhase(String phase, String frequency) {
        BigDecimal monthlyPrice;
        
        switch (phase) {
            case "PHASE_1":
                monthlyPrice = new BigDecimal("5.00");
                break;
            case "PHASE_2":
                monthlyPrice = new BigDecimal("10.00");
                break;
            case "PHASE_3":
                monthlyPrice = new BigDecimal("19.99");
                break;
            default:
                monthlyPrice = new BigDecimal("19.99");
        }

        if ("ANNUAL".equals(frequency)) {
            // Descuento de 2 meses al pagar anual (10 meses en vez de 12)
            return monthlyPrice.multiply(new BigDecimal("10"));
        }

        return monthlyPrice;
    }

    /**
     * Determina el plan según la fase
     */
    public static String getPlanByPhase(String phase) {
        switch (phase) {
            case "PHASE_1":
                return "PREMIUM_PHASE_1";
            case "PHASE_2":
                return "PREMIUM_PHASE_2";
            case "PHASE_3":
                return "PREMIUM_PHASE_3";
            default:
                return "FREE";
        }
    }

    /**
     * Cancela la membresía
     */
    public void cancel() {
        this.status = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * Suspende la membresía temporalmente
     */
    public void suspend() {
        this.status = "SUSPENDED";
        this.isActive = false;
    }

    /**
     * Reactiva la membresía
     */
    public void reactivate() {
        this.status = "ACTIVE";
        this.isActive = true;
        this.cancelledAt = null;
    }

    /**
     * Renueva la membresía extendiendo fecha de expiración
     */
    public void renew(int months) {
        if (this.expiresAt == null) {
            this.expiresAt = LocalDateTime.now().plusMonths(months);
        } else {
            this.expiresAt = this.expiresAt.plusMonths(months);
        }
        this.status = "ACTIVE";
        this.isActive = true;
    }
}
