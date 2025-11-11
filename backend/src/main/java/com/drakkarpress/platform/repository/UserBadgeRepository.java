package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {

    /**
     * Buscar badges activos de un usuario
     */
    List<UserBadge> findByUserIdAndStatus(UUID userId, String status);

    /**
     * Buscar todos los badges de un usuario
     */
    List<UserBadge> findByUserIdOrderByAwardedAtDesc(UUID userId);

    /**
     * Verificar si usuario tiene badge específico
     */
    boolean existsByUserIdAndBadgeIdAndStatus(UUID userId, UUID badgeId, String status);

    /**
     * Buscar badge específico de usuario
     */
    Optional<UserBadge> findByUserIdAndBadgeId(UUID userId, UUID badgeId);

    /**
     * Contar badges activos por usuario
     */
    long countByUserIdAndStatus(UUID userId, String status);

    /**
     * Buscar badges auto-asignados
     */
    List<UserBadge> findByIsAutoAssignedTrue();

    /**
     * Estadísticas de badges por tipo
     */
    @Query("SELECT ub.badge.code, COUNT(ub) FROM UserBadge ub WHERE ub.status = 'ACTIVE' GROUP BY ub.badge.code")
    List<Object[]> countBadgesByType();
}
