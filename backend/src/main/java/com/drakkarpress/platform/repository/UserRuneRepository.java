package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.UserRune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRuneRepository extends JpaRepository<UserRune, UUID> {

    /**
     * Buscar runa activa del usuario
     */
    Optional<UserRune> findByUserIdAndIsActiveTrue(UUID userId);

    /**
     * Buscar historial de runas del usuario
     */
    List<UserRune> findByUserIdOrderBySelectedAtDesc(UUID userId);

    /**
     * Verificar si usuario puede cambiar runa (última selección > 30 días)
     */
    @Query("SELECT COUNT(ur) = 0 FROM UserRune ur WHERE ur.user.id = :userId AND ur.isActive = true AND ur.selectedAt > :oneMonthAgo")
    boolean canUserChangeRune(@Param("userId") UUID userId, @Param("oneMonthAgo") LocalDateTime oneMonthAgo);

    /**
     * Contar usuarios con cada runa
     */
    @Query("SELECT ur.rune.id, COUNT(ur) FROM UserRune ur WHERE ur.isActive = true GROUP BY ur.rune.id")
    List<Object[]> countUsersByRune();

    /**
     * Buscar usuarios con runa específica
     */
    List<UserRune> findByRuneIdAndIsActiveTrue(UUID runeId);
}
