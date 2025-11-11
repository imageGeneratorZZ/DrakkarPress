package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.UserActivityFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityFeedRepository extends JpaRepository<UserActivityFeed, UUID> {

    /**
     * Buscar actividad de un usuario
     */
    List<UserActivityFeed> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Buscar posts públicos
     */
    List<UserActivityFeed> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * Buscar por tipo de actividad
     */
    List<UserActivityFeed> findByActivityTypeAndIsPublicTrueOrderByCreatedAtDesc(String activityType);

    /**
     * Feed personalizado (posts de usuarios seguidos)
     */
    @Query("SELECT uaf FROM UserActivityFeed uaf WHERE uaf.user.id IN (SELECT c.followed.id FROM Connection c WHERE c.follower.id = :userId AND c.connectionStatus = 'ACCEPTED') OR uaf.user.id = :userId ORDER BY uaf.createdAt DESC")
    List<UserActivityFeed> findPersonalizedFeed(@Param("userId") UUID userId);

    /**
     * Posts destacados (pinned)
     */
    List<UserActivityFeed> findByUserIdAndIsPinnedTrueOrderByCreatedAtDesc(UUID userId);

    /**
     * Posts recientes
     */
    @Query("SELECT uaf FROM UserActivityFeed uaf WHERE uaf.createdAt >= :since AND uaf.isPublic = true ORDER BY uaf.createdAt DESC")
    List<UserActivityFeed> findRecentPosts(@Param("since") LocalDateTime since);

    /**
     * Posts más populares (engagement)
     */
    @Query("SELECT uaf FROM UserActivityFeed uaf WHERE uaf.isPublic = true ORDER BY (uaf.likesCount + uaf.commentsCount + uaf.sharesCount) DESC")
    List<UserActivityFeed> findMostPopular();

    /**
     * Contar posts de usuario
     */
    long countByUserId(UUID userId);
}
