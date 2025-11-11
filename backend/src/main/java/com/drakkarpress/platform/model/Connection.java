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
 * Entidad Connection - Conexión social entre usuarios
 * 
 * Características principales:
 * - Modelo follower/followed (estilo Twitter)
 * - Estados: PENDING, ACCEPTED, BLOCKED
 * - Red de networking entre escritores, editoriales, imprentas, revendedores
 */
@Entity
@Table(name = "connections", indexes = {
    @Index(name = "idx_connections_follower_id", columnList = "follower_id"),
    @Index(name = "idx_connections_followed_id", columnList = "followed_id"),
    @Index(name = "idx_connections_status", columnList = "connection_status"),
    @Index(name = "idx_connections_created_at", columnList = "created_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_follower_followed", columnNames = {"follower_id", "followed_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que sigue (follower)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    /**
     * Usuario seguido (followed)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    /**
     * Estado de la conexión
     * PENDING: solicitud pendiente (para cuentas privadas)
     * ACCEPTED: conexión activa
     * BLOCKED: bloqueado
     */
    @Column(name = "connection_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACCEPTED'")
    private String connectionStatus;

    /**
     * Notas privadas del follower sobre el followed
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Si las notificaciones están activas para esta conexión
     */
    @Column(name = "notifications_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean notificationsEnabled;

    /**
     * Fecha de la conexión
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de aceptación (si fue pending)
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    /**
     * Fecha de bloqueo (si aplica)
     */
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si la conexión está activa
     */
    public boolean isActive() {
        return "ACCEPTED".equals(connectionStatus);
    }

    /**
     * Verifica si está pendiente
     */
    public boolean isPending() {
        return "PENDING".equals(connectionStatus);
    }

    /**
     * Verifica si está bloqueado
     */
    public boolean isBlocked() {
        return "BLOCKED".equals(connectionStatus);
    }

    /**
     * Acepta la conexión
     */
    public void accept() {
        this.connectionStatus = "ACCEPTED";
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * Bloquea la conexión
     */
    public void block() {
        this.connectionStatus = "BLOCKED";
        this.blockedAt = LocalDateTime.now();
        this.notificationsEnabled = false;
    }

    /**
     * Desbloquea y acepta
     */
    public void unblock() {
        this.connectionStatus = "ACCEPTED";
        this.blockedAt = null;
        this.notificationsEnabled = true;
    }

    /**
     * Activa notificaciones
     */
    public void enableNotifications() {
        this.notificationsEnabled = true;
    }

    /**
     * Desactiva notificaciones
     */
    public void disableNotifications() {
        this.notificationsEnabled = false;
    }

    /**
     * Días desde la conexión
     */
    public long getDaysSinceConnection() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }

    /**
     * Verifica si es una conexión reciente (menos de 30 días)
     */
    public boolean isRecent() {
        return getDaysSinceConnection() <= 30;
    }

    /**
     * Verifica si es una conexión mutua
     * (Requiere query adicional para verificar si followed también sigue a follower)
     */
    public boolean isMutual(Connection reverseConnection) {
        if (reverseConnection == null) {
            return false;
        }
        return reverseConnection.getFollower().getId().equals(this.followed.getId())
                && reverseConnection.getFollowed().getId().equals(this.follower.getId())
                && reverseConnection.isActive()
                && this.isActive();
    }

    /**
     * Obtiene el username del follower
     */
    public String getFollowerUsername() {
        return follower != null ? follower.getUsername() : null;
    }

    /**
     * Obtiene el username del followed
     */
    public String getFollowedUsername() {
        return followed != null ? followed.getUsername() : null;
    }

    /**
     * Crea una nueva conexión aceptada
     */
    public static Connection createAccepted(User follower, User followed) {
        return Connection.builder()
                .follower(follower)
                .followed(followed)
                .connectionStatus("ACCEPTED")
                .acceptedAt(LocalDateTime.now())
                .notificationsEnabled(true)
                .build();
    }

    /**
     * Crea una nueva conexión pendiente
     */
    public static Connection createPending(User follower, User followed) {
        return Connection.builder()
                .follower(follower)
                .followed(followed)
                .connectionStatus("PENDING")
                .notificationsEnabled(true)
                .build();
    }
}
