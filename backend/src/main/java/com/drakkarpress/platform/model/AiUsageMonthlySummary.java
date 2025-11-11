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
 * Entidad AiUsageMonthlySummary - Resumen mensual de uso de IA
 * 
 * Características principales:
 * - Agregación mensual por usuario y tipo de uso
 * - Permite verificar límites rápidamente sin escanear toda la tabla de tracking
 * - Reset automático cada mes
 */
@Entity
@Table(name = "ai_usage_monthly_summary", indexes = {
    @Index(name = "idx_ai_usage_summary_user_id", columnList = "user_id"),
    @Index(name = "idx_ai_usage_summary_year_month", columnList = "year, month"),
    @Index(name = "idx_ai_usage_summary_usage_type", columnList = "usage_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_year_month_type", 
                     columnNames = {"user_id", "year", "month", "usage_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsageMonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Año
     */
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * Mes (1-12)
     */
    @Column(name = "month", nullable = false)
    private Integer month;

    /**
     * Tipo de uso de IA
     */
    @Column(name = "usage_type", nullable = false, length = 50)
    private String usageType;

    /**
     * Contador de usos en el mes
     */
    @Column(name = "usage_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer usageCount;

    /**
     * Total de tokens consumidos
     */
    @Column(name = "total_tokens", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalTokens;

    /**
     * Costo total estimado
     */
    @Column(name = "total_cost", precision = 10, scale = 4, columnDefinition = "DECIMAL(10,4) DEFAULT 0")
    private BigDecimal totalCost;

    /**
     * Número de usos exitosos
     */
    @Column(name = "successful_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer successfulCount;

    /**
     * Número de usos fallidos
     */
    @Column(name = "failed_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer failedCount;

    /**
     * Límite mensual del plan
     * NULL = ilimitado
     */
    @Column(name = "monthly_limit")
    private Integer monthlyLimit;

    /**
     * Última actualización del contador
     */
    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si alcanzó el límite
     */
    public boolean hasReachedLimit() {
        if (monthlyLimit == null) {
            return false; // Ilimitado
        }
        return usageCount >= monthlyLimit;
    }

    /**
     * Verifica si puede usar (no ha alcanzado límite)
     */
    public boolean canUse() {
        return !hasReachedLimit();
    }

    /**
     * Usos restantes
     */
    public Integer getRemainingUses() {
        if (monthlyLimit == null) {
            return null; // Ilimitado
        }
        int remaining = monthlyLimit - usageCount;
        return Math.max(0, remaining);
    }

    /**
     * Porcentaje de uso
     */
    public Double getUsagePercentage() {
        if (monthlyLimit == null || monthlyLimit == 0) {
            return 0.0;
        }
        return (usageCount.doubleValue() / monthlyLimit.doubleValue()) * 100.0;
    }

    /**
     * Incrementa el contador de uso
     */
    public void incrementUsage(boolean successful, Integer tokens, BigDecimal cost) {
        this.usageCount++;
        
        if (successful) {
            this.successfulCount++;
        } else {
            this.failedCount++;
        }
        
        if (tokens != null) {
            this.totalTokens = (this.totalTokens != null ? this.totalTokens : 0) + tokens;
        }
        
        if (cost != null) {
            this.totalCost = (this.totalCost != null ? this.totalCost : BigDecimal.ZERO).add(cost);
        }
        
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Resetea los contadores (al inicio de nuevo mes)
     */
    public void reset() {
        this.usageCount = 0;
        this.totalTokens = 0;
        this.totalCost = BigDecimal.ZERO;
        this.successfulCount = 0;
        this.failedCount = 0;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si es del mes actual
     */
    public boolean isCurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        return year.equals(now.getYear()) && month.equals(now.getMonthValue());
    }

    /**
     * Tasa de éxito
     */
    public Double getSuccessRate() {
        if (usageCount == 0) {
            return 0.0;
        }
        return (successfulCount.doubleValue() / usageCount.doubleValue()) * 100.0;
    }

    /**
     * Costo promedio por uso
     */
    public BigDecimal getAverageCost() {
        if (usageCount == 0 || totalCost == null) {
            return BigDecimal.ZERO;
        }
        return totalCost.divide(BigDecimal.valueOf(usageCount), 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Crea un nuevo resumen para el mes actual
     */
    public static AiUsageMonthlySummary createForCurrentMonth(
            User user, 
            String usageType, 
            Integer limit) {
        
        LocalDateTime now = LocalDateTime.now();
        return AiUsageMonthlySummary.builder()
                .user(user)
                .year(now.getYear())
                .month(now.getMonthValue())
                .usageType(usageType)
                .usageCount(0)
                .totalTokens(0)
                .totalCost(BigDecimal.ZERO)
                .successfulCount(0)
                .failedCount(0)
                .monthlyLimit(limit)
                .build();
    }

    /**
     * Formatea el costo total
     */
    public String getFormattedTotalCost() {
        if (totalCost == null) {
            return "$0.00";
        }
        return String.format("$%.4f", totalCost);
    }

    /**
     * Nombre del tipo de uso
     */
    public String getUsageTypeName() {
        switch (usageType) {
            case "FULL_BOOK_GENERATION":
                return "Generación completa";
            case "COVER_GENERATION":
                return "Portadas";
            case "WRITING_ASSISTANT":
                return "Asistente";
            case "TEXT_CORRECTION":
                return "Corrección";
            case "SERIES_GENERATION":
                return "Series";
            case "TRANSLATION":
                return "Traducción";
            default:
                return usageType;
        }
    }

    /**
     * Nombre del mes
     */
    public String getMonthName() {
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return months[month - 1];
    }
}
