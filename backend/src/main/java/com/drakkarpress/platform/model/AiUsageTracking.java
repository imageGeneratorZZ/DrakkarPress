package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad AiUsageTracking - Tracking detallado de uso de IA
 * 
 * Características principales:
 * - Registra cada uso de IA por usuario
 * - Guarda detalles: tipo, prompt, tokens, costo, resultado
 * - Para analytics, facturación y control de límites
 */
@Entity
@Table(name = "ai_usage_tracking", indexes = {
    @Index(name = "idx_ai_usage_tracking_user_id", columnList = "user_id"),
    @Index(name = "idx_ai_usage_tracking_usage_type", columnList = "usage_type"),
    @Index(name = "idx_ai_usage_tracking_used_at", columnList = "used_at"),
    @Index(name = "idx_ai_usage_tracking_year_month", columnList = "year, month")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsageTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que usó la IA
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tipo de uso de IA
     * FULL_BOOK_GENERATION, COVER_GENERATION, WRITING_ASSISTANT,
     * TEXT_CORRECTION, SERIES_GENERATION, TRANSLATION
     */
    @Column(name = "usage_type", nullable = false, length = 50)
    private String usageType;

    /**
     * Prompt o input del usuario
     */
    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    /**
     * Resultado generado (puede ser grande, considerar almacenamiento externo)
     */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    /**
     * Tokens consumidos (para APIs como OpenAI)
     */
    @Column(name = "tokens_used")
    private Integer tokensUsed;

    /**
     * Costo estimado en USD
     */
    @Column(name = "estimated_cost", precision = 10, scale = 4)
    private java.math.BigDecimal estimatedCost;

    /**
     * Modelo de IA usado
     * Ej: "gpt-4", "gpt-3.5-turbo", "dall-e-3", etc.
     */
    @Column(name = "model_used", length = 100)
    private String modelUsed;

    /**
     * Año del uso (para particionamiento)
     */
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * Mes del uso (1-12) (para particionamiento)
     */
    @Column(name = "month", nullable = false)
    private Integer month;

    /**
     * Si fue exitoso
     */
    @Column(name = "is_successful", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isSuccessful;

    /**
     * Mensaje de error (si falló)
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Metadata adicional en JSON
     * Ej: {"genre": "fantasy", "language": "es", "num_chapters": 10}
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    /**
     * Timestamp del uso
     */
    @CreationTimestamp
    @Column(name = "used_at", nullable = false, updatable = false)
    private LocalDateTime usedAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si fue exitoso
     */
    public boolean wasSuccessful() {
        return Boolean.TRUE.equals(isSuccessful);
    }

    /**
     * Verifica si falló
     */
    public boolean failed() {
        return !wasSuccessful();
    }

    /**
     * Obtiene el nombre del tipo de uso
     */
    public String getUsageTypeName() {
        switch (usageType) {
            case "FULL_BOOK_GENERATION":
                return "Generación completa";
            case "COVER_GENERATION":
                return "Portada";
            case "WRITING_ASSISTANT":
                return "Asistente";
            case "TEXT_CORRECTION":
                return "Corrección";
            case "SERIES_GENERATION":
                return "Serie";
            case "TRANSLATION":
                return "Traducción";
            default:
                return usageType;
        }
    }

    /**
     * Formatea el costo
     */
    public String getFormattedCost() {
        if (estimatedCost == null) {
            return "$0.00";
        }
        return String.format("$%.4f", estimatedCost);
    }

    /**
     * Crea registro de uso exitoso
     */
    public static AiUsageTracking createSuccessful(
            User user, 
            String usageType, 
            String prompt,
            String result,
            Integer tokens,
            String model) {
        
        LocalDateTime now = LocalDateTime.now();
        return AiUsageTracking.builder()
                .user(user)
                .usageType(usageType)
                .prompt(prompt)
                .result(result)
                .tokensUsed(tokens)
                .modelUsed(model)
                .year(now.getYear())
                .month(now.getMonthValue())
                .isSuccessful(true)
                .usedAt(now)
                .build();
    }

    /**
     * Crea registro de uso fallido
     */
    public static AiUsageTracking createFailed(
            User user,
            String usageType,
            String prompt,
            String errorMessage) {
        
        LocalDateTime now = LocalDateTime.now();
        return AiUsageTracking.builder()
                .user(user)
                .usageType(usageType)
                .prompt(prompt)
                .errorMessage(errorMessage)
                .year(now.getYear())
                .month(now.getMonthValue())
                .isSuccessful(false)
                .usedAt(now)
                .build();
    }

    /**
     * Longitud del prompt
     */
    public int getPromptLength() {
        return prompt != null ? prompt.length() : 0;
    }

    /**
     * Longitud del resultado
     */
    public int getResultLength() {
        return result != null ? result.length() : 0;
    }
}
