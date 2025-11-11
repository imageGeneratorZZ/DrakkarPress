package com.drakkarpress.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_generations")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationType type;

    @Enumerated(EnumType.STRING)
    private Book.Genre genre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column
    private Integer tokensUsed;

    @Column(nullable = false)
    @Builder.Default
    private Boolean success = true;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum GenerationType {
        IDEA,           // Generar idea de libro
        CHAPTER,        // Extender capítulo
        SYNOPSIS,       // Crear sinopsis
        TITLE,          // Sugerir títulos
        CHARACTER,      // Desarrollar personaje
        DIALOGUE,       // Generar diálogo
        SCENE,          // Desarrollar escena
        OUTLINE,        // Crear outline/estructura
        MARKETING_COPY  // Texto de marketing
    }
}
