package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.AiUsageMonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiUsageMonthlySummaryRepository extends JpaRepository<AiUsageMonthlySummary, UUID> {

    /**
     * Buscar resumen del mes actual por usuario y tipo
     */
    Optional<AiUsageMonthlySummary> findByUserIdAndYearAndMonthAndUsageType(
        UUID userId, Integer year, Integer month, String usageType
    );

    /**
     * Buscar resúmenes del mes actual por usuario
     */
    List<AiUsageMonthlySummary> findByUserIdAndYearAndMonth(UUID userId, Integer year, Integer month);

    /**
     * Buscar usuarios que alcanzaron límite
     */
    @Query("SELECT ums FROM AiUsageMonthlySummary ums WHERE ums.year = :year AND ums.month = :month AND ums.monthlyLimit IS NOT NULL AND ums.usageCount >= ums.monthlyLimit")
    List<AiUsageMonthlySummary> findUsersAtLimit(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Buscar usuarios cerca del límite (>80%)
     */
    @Query("SELECT ums FROM AiUsageMonthlySummary ums WHERE ums.year = :year AND ums.month = :month AND ums.monthlyLimit IS NOT NULL AND ums.usageCount >= (ums.monthlyLimit * 0.8)")
    List<AiUsageMonthlySummary> findUsersNearLimit(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Top usuarios por uso
     */
    @Query("SELECT ums FROM AiUsageMonthlySummary ums WHERE ums.year = :year AND ums.month = :month ORDER BY ums.usageCount DESC")
    List<AiUsageMonthlySummary> findTopUsers(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Estadísticas globales del mes
     */
    @Query("SELECT ums.usageType, SUM(ums.usageCount), SUM(ums.totalTokens), SUM(ums.totalCost) FROM AiUsageMonthlySummary ums WHERE ums.year = :year AND ums.month = :month GROUP BY ums.usageType")
    List<Object[]> getMonthlyStatistics(@Param("year") Integer year, @Param("month") Integer month);
}
