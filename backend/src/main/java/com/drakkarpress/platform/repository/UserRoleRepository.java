package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    /**
     * Buscar roles activos de un usuario
     */
    List<UserRole> findByUserIdAndIsActiveTrue(UUID userId);

    /**
     * Buscar rol específico de usuario
     */
    Optional<UserRole> findByUserIdAndRoleType(UUID userId, String roleType);

    /**
     * Verificar si usuario tiene rol
     */
    boolean existsByUserIdAndRoleTypeAndIsActiveTrue(UUID userId, String roleType);

    /**
     * Buscar usuarios con rol específico
     */
    List<UserRole> findByRoleTypeAndIsActiveTrue(String roleType);

    /**
     * Buscar roles pendientes de verificación
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.requiresVerification = true AND ur.isVerified = false AND ur.isActive = true")
    List<UserRole> findPendingVerification();

    /**
     * Buscar autores/editoriales por tipo de entidad
     */
    List<UserRole> findByRoleTypeAndEntityTypeAndIsActiveTrue(String roleType, String entityType);

    /**
     * Contar usuarios por rol
     */
    long countByRoleTypeAndIsActiveTrue(String roleType);

    /**
     * Buscar roles verificados
     */
    List<UserRole> findByIsVerifiedTrueAndIsActiveTrue();
}
