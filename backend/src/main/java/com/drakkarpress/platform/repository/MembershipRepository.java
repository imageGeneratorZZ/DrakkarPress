package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    /**
     * Buscar membresía por usuario
     */
    Optional<Membership> findByUserId(UUID userId);

    /**
     * Buscar membresías por plan
     */
    List<Membership> findByPlan(String plan);

    /**
     * Buscar membresías activas por plan
     */
    List<Membership> findByPlanAndIsActiveTrue(String plan);

    /**
     * Buscar membresías grandfathered
     */
    List<Membership> findByIsGrandfatheredTrueAndIsActiveTrue();

    /**
     * Buscar membresías de cortesía
     */
    List<Membership> findByIsCourtesyTrueAndIsActiveTrue();

    /**
     * Buscar membresías que expiran pronto
     */
    @Query("SELECT m FROM Membership m WHERE m.expiresAt IS NOT NULL AND m.expiresAt BETWEEN :now AND :futureDate AND m.isActive = true")
    List<Membership> findExpiringSoon(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    /**
     * Buscar membresías expiradas pero aún activas (para limpieza)
     */
    @Query("SELECT m FROM Membership m WHERE m.expiresAt IS NOT NULL AND m.expiresAt < :now AND m.isActive = true")
    List<Membership> findExpiredButActive(@Param("now") LocalDateTime now);

    /**
     * Contar membresías por plan
     */
    long countByPlan(String plan);

    /**
     * Contar membresías activas
     */
    long countByIsActiveTrue();

    /**
     * Revenue total estimado (suma de precios pagados)
     */
    @Query("SELECT COALESCE(SUM(m.pricePaid), 0) FROM Membership m WHERE m.isActive = true AND m.pricePaid IS NOT NULL")
    java.math.BigDecimal calculateTotalRevenue();
}
