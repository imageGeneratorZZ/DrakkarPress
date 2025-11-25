package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Capítulo editable dentro de un BookProject.
 */
@Entity
@Table(name = "book_project_chapters", indexes = {
        @Index(name = "idx_book_chapter_project", columnList = "project_id"),
        @Index(name = "idx_book_chapter_order", columnList = "chapter_order")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private BookProject project;

    @Column(name = "chapter_order", nullable = false)
    private Integer chapterOrder; // 1..N

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ChapterStatus status = ChapterStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ChapterStatus {
        DRAFT,
        GENERATED,
        EDITED,
        NEEDS_REVIEW,  // Capítulo generado pero necesita revisión por cambios en capítulos anteriores
        PENDING        // Esperando generación
    }
}
