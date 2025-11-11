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
 * Entidad UserRune - Relación Usuario-Runa
 * 
 * Características principales:
 * - Solo usuarios premium pueden seleccionar runa
 * - Límite de cambio: 1 vez al mes
 * - La runa representa la identidad del creador
 * - Aparece en perfil, posts, comentarios
 */
@Entity
@Table(name = "user_runes", indexes = {
    @Index(name = "idx_user_runes_user_id", columnList = "user_id"),
    @Index(name = "idx_user_runes_rune_id", columnList = "rune_id"),
    @Index(name = "idx_user_runes_is_active", columnList = "is_active"),
    @Index(name = "idx_user_runes_selected_at", columnList = "selected_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRune {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que seleccionó la runa
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Runa seleccionada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rune_id", nullable = false)
    private Rune rune;

    /**
     * Si esta runa está activa para el usuario
     * Solo puede haber 1 runa activa por usuario
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * Fecha en que se seleccionó esta runa
     */
    @Column(name = "selected_at", nullable = false)
    private LocalDateTime selectedAt;

    /**
     * Fecha en que se desactivó (cambió por otra)
     */
    @Column(name = "deselected_at")
    private LocalDateTime deselectedAt;

    /**
     * Razón del cambio (opcional)
     */
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

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
     * Verifica si puede cambiar la runa (límite 1 vez al mes)
     * @return true si han pasado 30 días desde la última selección
     */
    public boolean canChange() {
        if (selectedAt == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(selectedAt.plusMonths(1));
    }

    /**
     * Desactiva esta runa (cuando el usuario selecciona otra)
     */
    public void deactivate(String reason) {
        this.isActive = false;
        this.deselectedAt = LocalDateTime.now();
        this.changeReason = reason;
    }

    /**
     * Obtiene el símbolo de la runa para mostrar en UI
     */
    public String getRuneSymbol() {
        return rune != null ? rune.getSymbol() : null;
    }

    /**
     * Obtiene el nombre de la runa
     */
    public String getRuneName() {
        return rune != null ? rune.getName() : null;
    }

    /**
     * Días desde que se seleccionó esta runa
     */
    public long getDaysSinceSelection() {
        if (selectedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(selectedAt, LocalDateTime.now());
    }

    /**
     * Días restantes hasta poder cambiar (30 días límite)
     */
    public long getDaysUntilCanChange() {
        if (canChange()) {
            return 0;
        }
        LocalDateTime canChangeAt = selectedAt.plusMonths(1);
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), canChangeAt);
    }
}
