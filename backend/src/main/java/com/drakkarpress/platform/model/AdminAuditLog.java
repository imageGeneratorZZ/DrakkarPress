package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad AdminAuditLog - Log de auditoría de acciones administrativas
 * 
 * Características principales:
 * - Registra todas las acciones de administradores
 * - Inmutable (no se puede editar/borrar)
 * - Para compliance y seguridad
 */
@Entity
@Table(name = "admin_audit_log", indexes = {
    @Index(name = "idx_admin_audit_log_admin_id", columnList = "admin_user_id"),
    @Index(name = "idx_admin_audit_log_action_type", columnList = "action_type"),
    @Index(name = "idx_admin_audit_log_target_user_id", columnList = "target_user_id"),
    @Index(name = "idx_admin_audit_log_performed_at", columnList = "performed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Admin que realizó la acción
     */
    @Column(name = "admin_user_id", nullable = false)
    private UUID adminUserId;

    /**
     * Tipo de acción
     * GRANT_PREMIUM, REVOKE_PREMIUM, ASSIGN_BADGE, REVOKE_BADGE,
     * VERIFY_ROLE, REJECT_VERIFICATION, SUSPEND_USER, ACTIVATE_USER,
     * DELETE_USER, MODIFY_MEMBERSHIP, REFUND_PAYMENT, etc.
     */
    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    /**
     * Descripción de la acción
     */
    @Column(name = "action_description", columnDefinition = "TEXT", nullable = false)
    private String actionDescription;

    /**
     * Usuario afectado (si aplica)
     */
    @Column(name = "target_user_id")
    private UUID targetUserId;

    /**
     * Tipo de entidad afectada
     * USER, MEMBERSHIP, BADGE, ROLE, PAYMENT, etc.
     */
    @Column(name = "target_entity_type", length = 50)
    private String targetEntityType;

    /**
     * ID de entidad afectada
     */
    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    /**
     * Valores antes del cambio (JSON)
     */
    @Column(name = "old_values", columnDefinition = "JSONB")
    private String oldValues;

    /**
     * Valores después del cambio (JSON)
     */
    @Column(name = "new_values", columnDefinition = "JSONB")
    private String newValues;

    /**
     * IP del admin
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent del admin
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Razón de la acción
     */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * Metadata adicional en JSON
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    /**
     * Timestamp de la acción (inmutable)
     */
    @CreationTimestamp
    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si es una acción de membresía
     */
    public boolean isMembershipAction() {
        return actionType != null && (
                actionType.contains("PREMIUM") || 
                actionType.contains("MEMBERSHIP") ||
                actionType.equals("MODIFY_MEMBERSHIP")
        );
    }

    /**
     * Verifica si es una acción de badge
     */
    public boolean isBadgeAction() {
        return actionType != null && actionType.contains("BADGE");
    }

    /**
     * Verifica si es una acción de verificación
     */
    public boolean isVerificationAction() {
        return actionType != null && (
                actionType.contains("VERIFY") || 
                actionType.contains("VERIFICATION")
        );
    }

    /**
     * Verifica si es una acción de usuario
     */
    public boolean isUserAction() {
        return actionType != null && (
                actionType.contains("USER") ||
                actionType.equals("SUSPEND_USER") ||
                actionType.equals("ACTIVATE_USER") ||
                actionType.equals("DELETE_USER")
        );
    }

    /**
     * Verifica si es una acción de pago
     */
    public boolean isPaymentAction() {
        return actionType != null && (
                actionType.contains("PAYMENT") ||
                actionType.contains("REFUND")
        );
    }

    /**
     * Horas desde la acción
     */
    public long getHoursSinceAction() {
        if (performedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.HOURS.between(performedAt, LocalDateTime.now());
    }

    /**
     * Verifica si es reciente (menos de 24 horas)
     */
    public boolean isRecent() {
        return getHoursSinceAction() <= 24;
    }

    /**
     * Obtiene el nombre de la acción en español
     */
    public String getActionName() {
        switch (actionType) {
            case "GRANT_PREMIUM":
                return "Otorgar Premium";
            case "REVOKE_PREMIUM":
                return "Revocar Premium";
            case "ASSIGN_BADGE":
                return "Asignar Badge";
            case "REVOKE_BADGE":
                return "Revocar Badge";
            case "VERIFY_ROLE":
                return "Verificar Rol";
            case "REJECT_VERIFICATION":
                return "Rechazar Verificación";
            case "SUSPEND_USER":
                return "Suspender Usuario";
            case "ACTIVATE_USER":
                return "Activar Usuario";
            case "DELETE_USER":
                return "Eliminar Usuario";
            case "MODIFY_MEMBERSHIP":
                return "Modificar Membresía";
            case "REFUND_PAYMENT":
                return "Reembolsar Pago";
            default:
                return actionType;
        }
    }

    /**
     * Crea log de otorgar premium
     */
    public static AdminAuditLog createGrantPremium(
            UUID adminId,
            UUID targetUserId,
            String reason,
            String ipAddress) {
        
        return AdminAuditLog.builder()
                .adminUserId(adminId)
                .actionType("GRANT_PREMIUM")
                .actionDescription("Membresía premium otorgada por cortesía")
                .targetUserId(targetUserId)
                .targetEntityType("MEMBERSHIP")
                .reason(reason)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * Crea log de asignar badge
     */
    public static AdminAuditLog createAssignBadge(
            UUID adminId,
            UUID targetUserId,
            UUID badgeId,
            String badgeName,
            String reason,
            String ipAddress) {
        
        return AdminAuditLog.builder()
                .adminUserId(adminId)
                .actionType("ASSIGN_BADGE")
                .actionDescription("Badge '" + badgeName + "' asignado a usuario")
                .targetUserId(targetUserId)
                .targetEntityType("BADGE")
                .targetEntityId(badgeId)
                .reason(reason)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * Crea log de verificar rol
     */
    public static AdminAuditLog createVerifyRole(
            UUID adminId,
            UUID targetUserId,
            String roleType,
            UUID verificationId,
            String notes,
            String ipAddress) {
        
        return AdminAuditLog.builder()
                .adminUserId(adminId)
                .actionType("VERIFY_ROLE")
                .actionDescription("Rol '" + roleType + "' verificado")
                .targetUserId(targetUserId)
                .targetEntityType("ROLE_VERIFICATION")
                .targetEntityId(verificationId)
                .reason(notes)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * Crea log de suspender usuario
     */
    public static AdminAuditLog createSuspendUser(
            UUID adminId,
            UUID targetUserId,
            String reason,
            String ipAddress) {
        
        return AdminAuditLog.builder()
                .adminUserId(adminId)
                .actionType("SUSPEND_USER")
                .actionDescription("Usuario suspendido")
                .targetUserId(targetUserId)
                .targetEntityType("USER")
                .reason(reason)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * Crea log genérico
     */
    public static AdminAuditLog create(
            UUID adminId,
            String actionType,
            String description,
            UUID targetUserId,
            String reason,
            String ipAddress) {
        
        return AdminAuditLog.builder()
                .adminUserId(adminId)
                .actionType(actionType)
                .actionDescription(description)
                .targetUserId(targetUserId)
                .reason(reason)
                .ipAddress(ipAddress)
                .build();
    }
}
