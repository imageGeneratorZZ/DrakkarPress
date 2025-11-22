package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stories", indexes = {
        @Index(name = "idx_story_user", columnList = "user_id"),
        @Index(name = "idx_story_expires", columnList = "expires_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    public enum StoryType { TEXT_QUOTE, IMAGE_CONCEPT, AUDIO_SNIPPET }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoryType type;

    @Column(columnDefinition = "TEXT")
    private String contentText; // Para quotes

    @Column(length = 500)
    private String mediaUrl; // Imagen / audio

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 24h

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
