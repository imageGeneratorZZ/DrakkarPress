package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.RoleVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleVerificationRepository extends JpaRepository<RoleVerification, UUID> {

    /**
     * Buscar verificaciones por usuario
     */
    List<RoleVerification> findByUserIdOrderBySubmittedAtDesc(UUID userId);

    /**
     * Buscar verificación por usuario y rol
     */
    Optional<RoleVerification> findByUserIdAndRoleType(UUID userId, String roleType);

    /**
     * Buscar verificaciones pendientes
     */
    List<RoleVerification> findByVerificationStatusOrderBySubmittedAtAsc(String status);

    /**
     * Buscar verificaciones que expiran pronto
     */
    @Query("SELECT rv FROM RoleVerification rv WHERE rv.expiresAt IS NOT NULL AND rv.expiresAt BETWEEN :now AND :futureDate AND rv.verificationStatus = 'APPROVED'")
    List<RoleVerification> findExpiringSoon(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    /**
     * Buscar verificaciones expiradas
     */
    @Query("SELECT rv FROM RoleVerification rv WHERE rv.expiresAt IS NOT NULL AND rv.expiresAt < :now AND rv.verificationStatus = 'APPROVED'")
    List<RoleVerification> findExpired(@Param("now") LocalDateTime now);

    /**
     * Contar verificaciones pendientes
     */
    long countByVerificationStatus(String status);

    /**
     * Buscar por tipo de documento
     */
    List<RoleVerification> findByDocumentTypeAndVerificationStatus(String documentType, String status);
}
