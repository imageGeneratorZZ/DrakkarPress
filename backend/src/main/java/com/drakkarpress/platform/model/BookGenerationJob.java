package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para trackear trabajos de generación de libros con IA.
 * Permite procesamiento asíncrono y seguimiento de progreso.
 */
@Entity
@Table(name = "book_generation_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookGenerationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String prompt;  // Prompt del usuario: "Escribe una novela de fantasía sobre..."

    @Column(name = "target_chapters")
    @Builder.Default
    private Integer targetChapters = 10;  // Número de capítulos a generar

    @Column(name = "target_words_per_chapter")
    @Builder.Default
    private Integer targetWordsPerChapter = 2000;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "progress_percentage")
    @Builder.Default
    private Integer progressPercentage = 0;  // 0-100

    @Column(name = "current_chapter")
    @Builder.Default
    private Integer currentChapter = 0;

    @Column(columnDefinition = "TEXT")
    private String metadata;  // JSON: título, autor, género, sinopsis generados por IA

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;  // URL de la portada generada

    @Column(name = "epub_path", length = 500)
    private String epubPath;  // Ruta al EPUB generado

    @Column(name = "book_id")
    private UUID bookId;  // ID del libro creado en Book entity

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "ai_model", length = 50)
    @Builder.Default
    private String aiModel = "gpt-4";  // "gpt-4", "claude-3-opus", "gemini-pro"

    @Column(name = "total_tokens_used")
    @Builder.Default
    private Long totalTokensUsed = 0L;

    @Column(name = "estimated_cost")
    @Builder.Default
    private Double estimatedCost = 0.0;  // USD

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum JobStatus {
        PENDING,        // En cola
        GENERATING,     // Generando contenido
        PROCESSING,     // Procesando EPUB
        COMPLETED,      // Terminado exitosamente
        FAILED,         // Error fatal
        CANCELLED       // Cancelado por usuario
    }

    public boolean isCompleted() {
        return status == JobStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == JobStatus.FAILED;
    }

    public void updateProgress(int chapter, int totalChapters) {
        this.currentChapter = chapter;
        this.progressPercentage = (chapter * 100) / totalChapters;
    }
}
