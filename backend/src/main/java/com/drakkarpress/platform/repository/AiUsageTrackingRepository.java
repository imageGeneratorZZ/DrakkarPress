package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.AiUsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AiUsageTrackingRepository extends JpaRepository<AiUsageTracking, UUID> {

    /**
     * Buscar uso por usuario
     */
    List<AiUsageTracking> findByUserIdOrderByUsedAtDesc(UUID userId);

    /**
     * Buscar uso por usuario y tipo
     */
    List<AiUsageTracking> findByUserIdAndUsageTypeOrderByUsedAtDesc(UUID userId, String usageType);

    /**
     * Buscar uso del mes actual por usuario y tipo
     */
    @Query("SELECT aut FROM AiUsageTracking aut WHERE aut.user.id = :userId AND aut.usageType = :usageType AND aut.year = :year AND aut.month = :month")
    List<AiUsageTracking> findByUserAndTypeAndMonth(
        @Param("userId") UUID userId,
        @Param("usageType") String usageType,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * Contar usos del mes actual
     */
    @Query("SELECT COUNT(aut) FROM AiUsageTracking aut WHERE aut.user.id = :userId AND aut.usageType = :usageType AND aut.year = :year AND aut.month = :month")
    long countCurrentMonthUsage(
        @Param("userId") UUID userId,
        @Param("usageType") String usageType,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * Buscar usos fallidos
     */
    List<AiUsageTracking> findByIsSuccessfulFalseOrderByUsedAtDesc();

    /**
     * Estadísticas de uso por tipo
     */
    @Query("SELECT aut.usageType, COUNT(aut), SUM(aut.tokensUsed) FROM AiUsageTracking aut WHERE aut.usedAt >= :since GROUP BY aut.usageType")
    List<Object[]> getUsageStatistics(@Param("since") LocalDateTime since);

    /**
     * Costo total por usuario
     */
    @Query("SELECT COALESCE(SUM(aut.estimatedCost), 0) FROM AiUsageTracking aut WHERE aut.user.id = :userId AND aut.usedAt >= :since")
    java.math.BigDecimal calculateTotalCost(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
}
