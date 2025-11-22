package com.drakkarpress.platform.model;

import com.drakkarpress.model.Book;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chapters", indexes = {
        @Index(name = "idx_chapters_book", columnList = "book_id"),
        @Index(name = "idx_chapters_number", columnList = "chapter_number")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    @Column(columnDefinition = "TEXT")
    private String originalContent;

    @Column(columnDefinition = "TEXT")
    private String editedContent;

    @Column(name = "is_regenerated", nullable = false)
    @Builder.Default
    private boolean regenerated = false;

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getEffectiveContent() { return editedContent != null ? editedContent : originalContent; }
}
