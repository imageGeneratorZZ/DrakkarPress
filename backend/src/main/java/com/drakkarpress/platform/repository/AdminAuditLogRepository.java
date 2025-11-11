package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, UUID> {

    /**
     * Buscar logs de un admin
     */
    List<AdminAuditLog> findByAdminUserIdOrderByPerformedAtDesc(UUID adminId);

    /**
     * Buscar logs por tipo de acción
     */
    List<AdminAuditLog> findByActionTypeOrderByPerformedAtDesc(String actionType);

    /**
     * Buscar logs sobre un usuario específico
     */
    List<AdminAuditLog> findByTargetUserIdOrderByPerformedAtDesc(UUID targetUserId);

    /**
     * Buscar logs por rango de fechas
     */
    List<AdminAuditLog> findByPerformedAtBetweenOrderByPerformedAtDesc(LocalDateTime start, LocalDateTime end);

    /**
     * Logs recientes
     */
    @Query("SELECT aal FROM AdminAuditLog aal WHERE aal.performedAt >= :since ORDER BY aal.performedAt DESC")
    List<AdminAuditLog> findRecentLogs(@Param("since") LocalDateTime since);

    /**
     * Estadísticas de acciones por admin
     */
    @Query("SELECT aal.adminUserId, aal.actionType, COUNT(aal) FROM AdminAuditLog aal GROUP BY aal.adminUserId, aal.actionType")
    List<Object[]> getAdminStatistics();

    /**
     * Acciones por tipo y período
     */
    @Query("SELECT aal.actionType, COUNT(aal) FROM AdminAuditLog aal WHERE aal.performedAt >= :since GROUP BY aal.actionType")
    List<Object[]> getActionStatistics(@Param("since") LocalDateTime since);

    /**
     * Buscar por IP
     */
    List<AdminAuditLog> findByIpAddressOrderByPerformedAtDesc(String ipAddress);
}
