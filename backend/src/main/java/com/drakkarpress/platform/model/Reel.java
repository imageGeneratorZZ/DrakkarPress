package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reels", indexes = {
        @Index(name = "idx_reel_user", columnList = "user_id"),
        @Index(name = "idx_reel_created", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String mediaUrl; // Video/Audio

    @Column(length = 255)
    private String caption;

    @Column
    private Integer durationSeconds;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer likes;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer comments;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer shares;

        public void incrementLikes() { if (likes == null) likes = 0; likes++; }
        public void decrementLikes() { if (likes != null && likes > 0) likes--; }
        public void incrementComments() { if (comments == null) comments = 0; comments++; }
        public void decrementComments() { if (comments != null && comments > 0) comments--; }
        public void incrementShares() { if (shares == null) shares = 0; shares++; }
    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}
