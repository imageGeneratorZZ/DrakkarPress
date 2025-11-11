package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad UserBadge - Relación Usuario-Badge
 * 
 * Características principales:
 * - Badges auto-asignados por triggers (Fundador, Early Adopter)
 * - Badges manuales por admin (Verified, Bestseller, Prolific)
 * - Pueden estar activos, inactivos o revocados
 * - Aparecen en perfil y posts del usuario
 */
@Entity
@Table(name = "user_badges", indexes = {
    @Index(name = "idx_user_badges_user_id", columnList = "user_id"),
    @Index(name = "idx_user_badges_badge_id", columnList = "badge_id"),
    @Index(name = "idx_user_badges_status", columnList = "status"),
    @Index(name = "idx_user_badges_awarded_at", columnList = "awarded_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que recibe el badge
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Badge otorgado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    /**
     * Estado del badge
     * ACTIVE: visible y activo
     * INACTIVE: oculto temporalmente
     * REVOKED: quitado permanentemente
     */
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status;

    /**
     * Fecha en que se otorgó el badge
     */
    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt;

    /**
     * Si fue otorgado automáticamente por el sistema
     * true: auto-asignado por trigger (Fundador, Early Adopter, Premium)
     * false: asignado manualmente por admin
     */
    @Column(name = "is_auto_assigned", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAutoAssigned;

    /**
     * Admin que otorgó el badge (si aplica)
     * NULL si fue auto-asignado
     */
    @Column(name = "awarded_by")
    private UUID awardedBy;

    /**
     * Razón por la que se otorgó el badge
     * Ej: "Fundador - Usuario #523", "Bestseller - 10,000 ventas", etc.
     */
    @Column(name = "award_reason", columnDefinition = "TEXT")
    private String awardReason;

    /**
     * Fecha de revocación (si aplica)
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Admin que revocó el badge (si aplica)
     */
    @Column(name = "revoked_by")
    private UUID revokedBy;

    /**
     * Razón de revocación (si aplica)
     */
    @Column(name = "revoke_reason", columnDefinition = "TEXT")
    private String revokeReason;

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
     * Verifica si el badge está activo
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * Verifica si el badge está revocado
     */
    public boolean isRevoked() {
        return "REVOKED".equals(status);
    }

    /**
     * Activa el badge
     */
    public void activate() {
        this.status = "ACTIVE";
    }

    /**
     * Desactiva el badge temporalmente
     */
    public void deactivate() {
        this.status = "INACTIVE";
    }

    /**
     * Revoca el badge permanentemente
     */
    public void revoke(UUID adminId, String reason) {
        this.status = "REVOKED";
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = adminId;
        this.revokeReason = reason;
    }

    /**
     * Obtiene el código del badge
     */
    public String getBadgeCode() {
        return badge != null ? badge.getCode() : null;
    }

    /**
     * Obtiene el icono del badge
     */
    public String getBadgeIcon() {
        return badge != null ? badge.getIcon() : null;
    }

    /**
     * Obtiene el nombre del badge en español
     */
    public String getBadgeNameEs() {
        return badge != null ? badge.getNameEs() : null;
    }

    /**
     * Días desde que se otorgó el badge
     */
    public long getDaysSinceAwarded() {
        if (awardedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(awardedAt, LocalDateTime.now());
    }

    /**
     * Verifica si es un badge de fase (Fundador o Early Adopter)
     */
    public boolean isPhaseBadge() {
        if (badge == null) return false;
        String code = badge.getCode();
        return "FOUNDER".equals(code) || "EARLY_ADOPTER".equals(code);
    }

    /**
     * Verifica si es un badge premium
     */
    public boolean isPremiumBadge() {
        if (badge == null) return false;
        return "PREMIUM".equals(badge.getCode());
    }
}
