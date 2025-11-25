package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BookProject representa un proyecto de libro en curso generado/gestionado con IA.
 * Se diferencia del modelo Book publicado: aquí se guarda el estado editable, capítulos
 * en borrador y metadatos iniciales antes de cualquier publicación/distribución.
 */
@Entity
@Table(name = "book_projects", indexes = {
        @Index(name = "idx_book_project_user", columnList = "owner_user_id"),
        @Index(name = "idx_book_project_created", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Usuario dueño del proyecto (referencia por ID para desacoplar de cascadas complejas)
    @Column(name = "owner_user_id", nullable = false, length = 36)
    private String ownerUserId; // UUID.toString()

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 100)
    private String genre;

    @Column(nullable = false, length = 150)
    private String style;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "planned_chapters")
    private Integer plannedChapters;

    @Column(name = "outline_generated", nullable = false)
    @Builder.Default
    private Boolean outlineGenerated = false;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("chapterOrder ASC")
    @Builder.Default
    private List<BookChapter> chapters = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addChapter(BookChapter ch) {
        ch.setProject(this);
        chapters.add(ch);
    }
}
