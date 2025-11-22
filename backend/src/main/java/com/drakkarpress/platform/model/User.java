package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad User - Usuario base del sistema DrakkarPress
 * 
 * Características principales:
 * - Perfil base CLIENT obligatorio
 * - Multi-rol: puede activar AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER
 * - user_number: para tracking de fases (1-1000, 1001-10000, 10001+)
 * - Relaciones: membresías, runas, badges, roles, conexiones
 */
@Entity(name = "PlatformUser")
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_username", columnList = "username", unique = true),
    @Index(name = "idx_users_user_number", columnList = "user_number"),
    @Index(name = "idx_users_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Número de usuario secuencial para determinar fase de membresía
     * 1-1,000: PHASE_1 ($5)
     * 1,001-10,000: PHASE_2 ($10)
     * 10,001+: PHASE_3 ($19.99)
     */
    @Column(name = "user_number", nullable = false, unique = true)
    private Long userNumber;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "language_preference", length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'es'")
    private String languagePreference;

    @Column(name = "is_email_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "role", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'READER'")
    @Builder.Default
    private String role = "READER";

    @Column(name = "subscription", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'FREE'")
    @Builder.Default
    private String subscription = "FREE";

    // ========================================================================
    // RELACIONES
    // ========================================================================

    /**
     * Membresía actual del usuario
     * Determina acceso a funciones premium y límites de IA
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Membership membership;

    /**
     * Roles adicionales activos del usuario
     * Ej: AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    /**
     * Runa seleccionada por el usuario (solo premium)
     * Representa su identidad como creador
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRune> userRunes = new HashSet<>();

    /**
     * Badges obtenidos por el usuario
     * Ej: Fundador, Early Adopter, Verified, Bestseller, etc.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserBadge> userBadges = new HashSet<>();

    /**
     * Verificaciones de documentos para roles especiales
     * AUTHOR_PUBLISHER: datos de pago
     * PRINT_SHOP: certificación
     * RESELLER: datos de pago
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<RoleVerification> roleVerifications = new HashSet<>();

    /**
     * Tracking de uso de IA del usuario
     * Para aplicar límites según plan (Free vs Premium)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AiUsageTracking> aiUsageHistory = new HashSet<>();

    /**
     * Conexiones sociales (followers/following)
     * Red de networking entre escritores, editoriales, imprentas, etc.
     */
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Connection> following = new HashSet<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Connection> followers = new HashSet<>();

    /**
     * Actividad del usuario en el feed
     * Posts, comentarios, reacciones
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserActivityFeed> activityFeed = new HashSet<>();

    /**
     * Mensajes enviados y recibidos
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Message> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Message> receivedMessages = new HashSet<>();

    /**
     * Transacciones de pago del usuario
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PaymentTransaction> paymentTransactions = new HashSet<>();

    /**
     * Sesiones activas del usuario (tokens JWT)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SessionToken> sessionTokens = new HashSet<>();

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Determina la fase del usuario según su user_number
     * @return PHASE_1, PHASE_2 o PHASE_3
     */
    public String determinePhase() {
        if (userNumber <= 1000) {
            return "PHASE_1"; // Fundadores
        } else if (userNumber <= 10000) {
            return "PHASE_2"; // Early Adopters
        } else {
            return "PHASE_3"; // Regular
        }
    }

    /**
     * Verifica si el usuario tiene un rol activo
     * @param roleType tipo de rol a verificar
     * @return true si tiene el rol activo
     */
    public boolean hasRole(String roleType) {
        return roles.stream()
                .anyMatch(role -> role.getRoleType().equals(roleType) && role.getIsActive());
    }

    /**
     * Verifica si el usuario es premium
     * @return true si tiene membresía premium activa
     */
    public boolean isPremium() {
        return membership != null 
                && membership.getIsActive() 
                && !membership.getPlan().equals("FREE");
    }

    /**
     * Soft delete - marca como eliminado sin borrar físicamente
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * Restaura usuario eliminado
     */
    public void restore() {
        this.deletedAt = null;
        this.isActive = true;
    }
}
