package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    /**
     * Buscar badge por código
     */
    Optional<Badge> findByCode(String code);

    /**
     * Buscar badges auto-asignables
     */
    List<Badge> findByAutoAssignTrue();

    /**
     * Buscar badges que requieren premium
     */
    List<Badge> findByRequiresPremiumTrue();

    /**
     * Buscar badges asociados a runa
     */
    List<Badge> findByRuneId(UUID runeId);

    /**
     * Verificar si código existe
     */
    boolean existsByCode(String code);
}
