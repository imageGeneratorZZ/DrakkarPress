package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.AiUsageLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiUsageLimitRepository extends JpaRepository<AiUsageLimit, UUID> {

    /**
     * Buscar límite por plan y tipo de uso
     */
    Optional<AiUsageLimit> findByPlanTypeAndUsageType(String planType, String usageType);

    /**
     * Buscar límites de un plan
     */
    List<AiUsageLimit> findByPlanTypeAndIsActiveTrue(String planType);

    /**
     * Buscar límites por tipo de uso
     */
    List<AiUsageLimit> findByUsageType(String usageType);

    /**
     * Buscar límites activos
     */
    List<AiUsageLimit> findByIsActiveTrue();

    /**
     * Verificar si existe límite
     */
    boolean existsByPlanTypeAndUsageType(String planType, String usageType);
}
