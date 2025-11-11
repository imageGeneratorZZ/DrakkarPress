package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, UUID> {

    /**
     * Buscar por refresh token hash
     */
    Optional<SessionToken> findByRefreshTokenHash(String refreshTokenHash);

    /**
     * Buscar sesiones activas de un usuario
     */
    List<SessionToken> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID userId);

    /**
     * Buscar todas las sesiones de un usuario
     */
    List<SessionToken> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Buscar sesiones expiradas pero aún activas
     */
    @Query("SELECT st FROM SessionToken st WHERE st.expiresAt < :now AND st.isActive = true")
    List<SessionToken> findExpiredButActive(@Param("now") LocalDateTime now);

    /**
     * Buscar sesiones inactivas (más de 30 días sin usar)
     */
    @Query("SELECT st FROM SessionToken st WHERE st.lastUsedAt < :threshold AND st.isActive = true")
    List<SessionToken> findInactiveSessions(@Param("threshold") LocalDateTime threshold);

    /**
     * Contar sesiones activas por usuario
     */
    long countByUserIdAndIsActiveTrue(UUID userId);

    /**
     * Revocar todas las sesiones de un usuario excepto una
     */
    @Query("SELECT st FROM SessionToken st WHERE st.user.id = :userId AND st.id != :exceptId AND st.isActive = true")
    List<SessionToken> findAllExcept(@Param("userId") UUID userId, @Param("exceptId") UUID exceptId);

    /**
     * Buscar por JTI
     */
    Optional<SessionToken> findByAccessTokenJti(String jti);

    /**
     * Sesiones por dispositivo
     */
    List<SessionToken> findByUserIdAndDeviceInfoContainingAndIsActiveTrue(UUID userId, String deviceKeyword);
}
