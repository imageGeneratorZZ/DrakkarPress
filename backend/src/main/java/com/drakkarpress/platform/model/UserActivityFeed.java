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
 * Entidad UserActivityFeed - Feed de actividad de usuarios
 * 
 * Características principales:
 * - Posts, comentarios, publicaciones de libros, logros
 * - Feed estilo Facebook/LinkedIn
 * - Likes, comentarios, compartir
 */
@Entity
@Table(name = "user_activity_feed", indexes = {
    @Index(name = "idx_activity_feed_user_id", columnList = "user_id"),
    @Index(name = "idx_activity_feed_activity_type", columnList = "activity_type"),
    @Index(name = "idx_activity_feed_created_at", columnList = "created_at"),
    @Index(name = "idx_activity_feed_is_public", columnList = "is_public")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que generó la actividad
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tipo de actividad
     * POST, COMMENT, BOOK_PUBLISHED, ACHIEVEMENT, BADGE_EARNED,
     * BOOK_REVIEW, CONNECTION_MADE, MILESTONE_REACHED
     */
    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    /**
     * Título de la actividad
     */
    @Column(name = "title", length = 255)
    private String title;

    /**
     * Contenido de la actividad
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * URL de imagen/media asociada
     */
    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    /**
     * ID de entidad relacionada (libro, badge, etc.)
     */
    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    /**
     * Tipo de entidad relacionada
     */
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    /**
     * Si es público o solo para conexiones
     */
    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isPublic;

    /**
     * Contador de likes
     */
    @Column(name = "likes_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer likesCount;

    /**
     * Contador de comentarios
     */
    @Column(name = "comments_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentsCount;

    /**
     * Contador de veces compartido
     */
    @Column(name = "shares_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer sharesCount;

    /**
     * Si está destacado (pinned)
     */
    @Column(name = "is_pinned", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPinned;

    /**
     * Metadata adicional en JSON
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si es un post
     */
    public boolean isPost() {
        return "POST".equals(activityType);
    }

    /**
     * Verifica si es publicación de libro
     */
    public boolean isBookPublished() {
        return "BOOK_PUBLISHED".equals(activityType);
    }

    /**
     * Verifica si es un logro/achievement
     */
    public boolean isAchievement() {
        return "ACHIEVEMENT".equals(activityType) || "BADGE_EARNED".equals(activityType);
    }

    /**
     * Incrementa contador de likes
     */
    public void incrementLikes() {
        this.likesCount++;
    }

    /**
     * Decrementa contador de likes
     */
    public void decrementLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    /**
     * Incrementa contador de comentarios
     */
    public void incrementComments() {
        this.commentsCount++;
    }

    /**
     * Decrementa contador de comentarios
     */
    public void decrementComments() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }

    /**
     * Incrementa contador de shares
     */
    public void incrementShares() {
        this.sharesCount++;
    }

    /**
     * Pin/unpin el post
     */
    public void togglePin() {
        this.isPinned = !this.isPinned;
    }

    /**
     * Fija el post
     */
    public void pin() {
        this.isPinned = true;
    }

    /**
     * Desfija el post
     */
    public void unpin() {
        this.isPinned = false;
    }

    /**
     * Engagement total (likes + comments + shares)
     */
    public Integer getTotalEngagement() {
        return likesCount + commentsCount + sharesCount;
    }

    /**
     * Tasa de engagement (por cada 100 vistas, necesitaría campo views_count)
     */
    public Double getEngagementRate(Integer viewsCount) {
        if (viewsCount == null || viewsCount == 0) {
            return 0.0;
        }
        return (getTotalEngagement().doubleValue() / viewsCount.doubleValue()) * 100.0;
    }

    /**
     * Horas desde creación
     */
    public long getHoursSinceCreation() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
    }

    /**
     * Verifica si es reciente (menos de 24 horas)
     */
    public boolean isRecent() {
        return getHoursSinceCreation() <= 24;
    }

    /**
     * Obtiene username del autor
     */
    public String getAuthorUsername() {
        return user != null ? user.getUsername() : null;
    }

    /**
     * Crea post simple
     */
    public static UserActivityFeed createPost(User user, String content, boolean isPublic) {
        return UserActivityFeed.builder()
                .user(user)
                .activityType("POST")
                .content(content)
                .isPublic(isPublic)
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .isPinned(false)
                .build();
    }

    /**
     * Crea anuncio de libro publicado
     */
    public static UserActivityFeed createBookPublished(
            User user, 
            String bookTitle, 
            UUID bookId,
            String coverUrl) {
        
        return UserActivityFeed.builder()
                .user(user)
                .activityType("BOOK_PUBLISHED")
                .title("¡Nuevo libro publicado!")
                .content("Acabo de publicar: " + bookTitle)
                .mediaUrl(coverUrl)
                .relatedEntityId(bookId)
                .relatedEntityType("BOOK")
                .isPublic(true)
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .isPinned(false)
                .build();
    }

    /**
     * Crea anuncio de badge ganado
     */
    public static UserActivityFeed createBadgeEarned(
            User user,
            String badgeName,
            UUID badgeId) {
        
        return UserActivityFeed.builder()
                .user(user)
                .activityType("BADGE_EARNED")
                .title("¡Nuevo badge!")
                .content("He ganado el badge: " + badgeName)
                .relatedEntityId(badgeId)
                .relatedEntityType("BADGE")
                .isPublic(true)
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .isPinned(false)
                .build();
    }
}
