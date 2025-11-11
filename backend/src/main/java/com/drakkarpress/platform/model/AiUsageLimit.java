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
 * Entidad AiUsageLimit - Límites de uso de IA por plan
 * 
 * Características principales:
 * - FREE: límites específicos por tipo de uso
 * - PREMIUM: NULL = ilimitado
 * - Tipos: generación completa, portadas, asistente, corrección, series, traducción
 */
@Entity
@Table(name = "ai_usage_limits", indexes = {
    @Index(name = "idx_ai_usage_limits_plan", columnList = "plan_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_plan_usage_type", columnNames = {"plan_type", "usage_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsageLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Tipo de plan
     * FREE, PREMIUM_PHASE_1, PREMIUM_PHASE_2, PREMIUM_PHASE_3, PREMIUM_COURTESY
     */
    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;

    /**
     * Tipo de uso de IA
     * FULL_BOOK_GENERATION, COVER_GENERATION, WRITING_ASSISTANT, 
     * TEXT_CORRECTION, SERIES_GENERATION, TRANSLATION
     */
    @Column(name = "usage_type", nullable = false, length = 50)
    private String usageType;

    /**
     * Límite mensual
     * NULL = ilimitado (todos los planes premium)
     * 0 = bloqueado (generación completa en FREE)
     * >0 = límite específico
     */
    @Column(name = "monthly_limit")
    private Integer monthlyLimit;

    /**
     * Descripción del límite
     */
    @Column(name = "description_es", columnDefinition = "TEXT")
    private String descriptionEs;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    /**
     * Si el límite está activo
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
     * Verifica si es ilimitado
     */
    public boolean isUnlimited() {
        return monthlyLimit == null;
    }

    /**
     * Verifica si está bloqueado
     */
    public boolean isBlocked() {
        return monthlyLimit != null && monthlyLimit == 0;
    }

    /**
     * Verifica si tiene límite específico
     */
    public boolean hasLimit() {
        return monthlyLimit != null && monthlyLimit > 0;
    }

    /**
     * Verifica si es plan gratuito
     */
    public boolean isFreePlan() {
        return "FREE".equals(planType);
    }

    /**
     * Verifica si es plan premium
     */
    public boolean isPremiumPlan() {
        return planType != null && planType.startsWith("PREMIUM_");
    }

    /**
     * Obtiene descripción formateada para mostrar
     */
    public String getFormattedLimit() {
        if (isUnlimited()) {
            return "Ilimitado";
        }
        if (isBlocked()) {
            return "Bloqueado";
        }
        return monthlyLimit + " por mes";
    }

    /**
     * Obtiene el nombre del tipo de uso en español
     */
    public String getUsageTypeName() {
        switch (usageType) {
            case "FULL_BOOK_GENERATION":
                return "Generación completa de libros";
            case "COVER_GENERATION":
                return "Generación de portadas";
            case "WRITING_ASSISTANT":
                return "Asistente de escritura";
            case "TEXT_CORRECTION":
                return "Corrección de textos";
            case "SERIES_GENERATION":
                return "Generación de series";
            case "TRANSLATION":
                return "Traducción";
            default:
                return usageType;
        }
    }

    /**
     * Límites por defecto para plan FREE
     */
    public static AiUsageLimit createFreeLimit(String usageType, Integer limit, String descEs, String descEn) {
        return AiUsageLimit.builder()
                .planType("FREE")
                .usageType(usageType)
                .monthlyLimit(limit)
                .descriptionEs(descEs)
                .descriptionEn(descEn)
                .isActive(true)
                .build();
    }

    /**
     * Límites por defecto para plan PREMIUM (ilimitado)
     */
    public static AiUsageLimit createPremiumLimit(String planType, String usageType) {
        return AiUsageLimit.builder()
                .planType(planType)
                .usageType(usageType)
                .monthlyLimit(null) // Ilimitado
                .descriptionEs("Uso ilimitado")
                .descriptionEn("Unlimited usage")
                .isActive(true)
                .build();
    }
}
