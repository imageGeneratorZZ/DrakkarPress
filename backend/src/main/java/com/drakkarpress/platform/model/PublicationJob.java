package com.drakkarpress.platform.model;

import com.drakkarpress.model.Book;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para trackear trabajos de publicación automática en plataformas externas.
 */
@Entity
@Table(name = "publication_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_platforms", length = 500)
    private String targetPlatforms;  // JSON: ["KDP", "GOOGLE_PLAY", "LULU"]

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PublicationStatus status = PublicationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String platformStatuses;  // JSON: {"KDP": "PUBLISHED", "GOOGLE_PLAY": "PENDING", ...}

    @Column(name = "kdp_asin", length = 20)
    private String kdpAsin;  // Amazon ASIN

    @Column(name = "google_play_id", length = 100)
    private String googlePlayId;

    @Column(name = "lulu_project_id", length = 100)
    private String luluProjectId;

    @Column(columnDefinition = "TEXT")
    private String conversionResults;  // JSON: rutas a MOBI, AZW3, PDF generados

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

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

    public enum PublicationStatus {
        PENDING,           // En cola
        CONVERTING,        // Convirtiendo formatos
        UPLOADING,         // Subiendo a plataformas
        PARTIALLY_PUBLISHED,  // Publicado en algunas plataformas
        COMPLETED,         // Publicado en todas
        FAILED             // Error fatal
    }

    public boolean isCompleted() {
        return status == PublicationStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PublicationStatus.FAILED;
    }
}
