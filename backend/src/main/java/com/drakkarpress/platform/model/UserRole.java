package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad UserRole - Roles activos del usuario
 * 
 * Características principales:
 * - Todos inician como CLIENT (obligatorio)
 * - Pueden activar roles adicionales: AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER
 * - Multi-rol: un usuario puede tener varios roles simultáneos
 * - Cada rol puede requerir verificación de documentos
 */
@Entity
@Table(name = "user_roles", indexes = {
    @Index(name = "idx_user_roles_user_id", columnList = "user_id"),
    @Index(name = "idx_user_roles_role_type", columnList = "role_type"),
    @Index(name = "idx_user_roles_is_active", columnList = "is_active")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario dueño del rol
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tipo de rol
     * CLIENT: base (todos lo tienen)
     * AUTHOR_PUBLISHER: escritor/editorial
     * PRINT_SHOP: imprenta
     * RESELLER: revendedor
     */
    @Column(name = "role_type", nullable = false, length = 50)
    private String roleType;

    /**
     * Para AUTHOR_PUBLISHER: tipo de entidad
     * INDIVIDUAL: persona (autor individual)
     * COMPANY: empresa (editorial)
     */
    @Column(name = "entity_type", length = 20)
    private String entityType;

    /**
     * Nombre legal/comercial (para COMPANY o PRINT_SHOP)
     */
    @Column(name = "business_name", length = 255)
    private String businessName;

    /**
     * Número de identificación fiscal
     */
    @Column(name = "tax_id", length = 100)
    private String taxId;

    /**
     * Si el rol está activo
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * Si el rol requiere verificación
     * AUTHOR_PUBLISHER: necesita datos de pago
     * PRINT_SHOP: necesita certificación
     * RESELLER: necesita datos de pago
     */
    @Column(name = "requires_verification", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean requiresVerification;

    /**
     * Si la verificación está completa
     */
    @Column(name = "is_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified;

    /**
     * Fecha en que se activó el rol
     */
    @Column(name = "activated_at", nullable = false)
    private LocalDateTime activatedAt;

    /**
     * Fecha en que se desactivó (si aplica)
     */
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si es el rol CLIENT
     */
    public boolean isClientRole() {
        return "CLIENT".equals(roleType);
    }

    /**
     * Verifica si es rol AUTHOR_PUBLISHER
     */
    public boolean isAuthorPublisher() {
        return "AUTHOR_PUBLISHER".equals(roleType);
    }

    /**
     * Verifica si es rol PRINT_SHOP
     */
    public boolean isPrintShop() {
        return "PRINT_SHOP".equals(roleType);
    }

    /**
     * Verifica si es rol RESELLER
     */
    public boolean isReseller() {
        return "RESELLER".equals(roleType);
    }

    /**
     * Verifica si es una empresa (para AUTHOR_PUBLISHER)
     */
    public boolean isCompany() {
        return "COMPANY".equals(entityType);
    }

    /**
     * Verifica si es un individuo (para AUTHOR_PUBLISHER)
     */
    public boolean isIndividual() {
        return "INDIVIDUAL".equals(entityType);
    }

    /**
     * Verifica si puede operar (activo y verificado si se requiere)
     */
    public boolean canOperate() {
        if (!isActive) {
            return false;
        }
        if (requiresVerification && !isVerified) {
            return false;
        }
        return true;
    }

    /**
     * Activa el rol
     */
    public void activate() {
        this.isActive = true;
        this.activatedAt = LocalDateTime.now();
        this.deactivatedAt = null;
    }

    /**
     * Desactiva el rol
     */
    public void deactivate() {
        this.isActive = false;
        this.deactivatedAt = LocalDateTime.now();
    }

    /**
     * Marca como verificado
     */
    public void verify() {
        this.isVerified = true;
    }

    /**
     * Remueve verificación
     */
    public void unverify() {
        this.isVerified = false;
    }

    /**
     * Obtiene nombre para mostrar según tipo de rol
     */
    public String getDisplayName() {
        if (businessName != null && !businessName.isEmpty()) {
            return businessName;
        }
        return user != null ? user.getFullName() : "Unknown";
    }

    /**
     * Días desde que se activó el rol
     */
    public long getDaysSinceActivation() {
        if (activatedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(activatedAt, LocalDateTime.now());
    }
}
