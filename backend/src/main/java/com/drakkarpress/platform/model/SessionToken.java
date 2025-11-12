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
 * Entidad SessionToken - Tokens JWT de sesión
 * 
 * Características principales:
 * - Almacena refresh tokens activos
 * - Permite invalidar sesiones remotamente
 * - Tracking de dispositivos y ubicaciones
 */
@Entity
@Table(name = "session_tokens", indexes = {
    @Index(name = "idx_session_tokens_user_id", columnList = "user_id"),
    @Index(name = "idx_session_tokens_refresh_token", columnList = "refresh_token_hash", unique = true),
    @Index(name = "idx_session_tokens_is_active", columnList = "is_active"),
    @Index(name = "idx_session_tokens_expires_at", columnList = "expires_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionToken {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario dueño de la sesión
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hash del refresh token (BCrypt)
     * NO almacenar el token en texto plano
     */
    @Column(name = "refresh_token_hash", nullable = false, unique = true, length = 255)
    private String refreshTokenHash;

    /**
     * JTI (JWT ID) del access token actual
     */
    @Column(name = "access_token_jti", length = 255)
    private String accessTokenJti;

    /**
     * Dispositivo/User Agent
     */
    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    /**
     * IP desde donde se creó la sesión
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Ubicación aproximada (país/ciudad)
     */
    @Column(name = "location", length = 255)
    private String location;

    /**
     * Si la sesión está activa
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * Fecha de expiración del refresh token
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Última actividad (último refresh)
     */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /**
     * Fecha de revocación (si aplica)
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Razón de revocación
     */
    @Column(name = "revoke_reason", length = 255)
    private String revokeReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Verifica si está expirado
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Verifica si es válido (activo y no expirado)
     */
    public boolean isValid() {
        return isActive && !isExpired();
    }

    /**
     * Revoca la sesión
     */
    public void revoke(String reason) {
        this.isActive = false;
        this.revokedAt = LocalDateTime.now();
        this.revokeReason = reason;
    }

    /**
     * Actualiza última actividad
     */
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Actualiza access token JTI
     */
    public void updateAccessTokenJti(String jti) {
        this.accessTokenJti = jti;
        updateLastUsed();
    }

    /**
     * Minutos hasta expiración
     */
    public long getMinutesUntilExpiry() {
        if (isExpired()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(LocalDateTime.now(), expiresAt);
    }

    /**
     * Horas desde creación
     */
    public long getHoursSinceCreation() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
    }

    /**
     * Minutos desde última actividad
     */
    public long getMinutesSinceLastUsed() {
        if (lastUsedAt == null) {
            return getHoursSinceCreation() * 60;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(lastUsedAt, LocalDateTime.now());
    }

    /**
     * Verifica si es una sesión inactiva (más de 30 días sin usar)
     */
    public boolean isInactive() {
        return getMinutesSinceLastUsed() > (30 * 24 * 60); // 30 días
    }

    /**
     * Extrae navegador del device info
     */
    public String getBrowser() {
        if (deviceInfo == null) {
            return "Unknown";
        }
        if (deviceInfo.contains("Chrome")) return "Chrome";
        if (deviceInfo.contains("Firefox")) return "Firefox";
        if (deviceInfo.contains("Safari")) return "Safari";
        if (deviceInfo.contains("Edge")) return "Edge";
        return "Other";
    }

    /**
     * Extrae OS del device info
     */
    public String getOperatingSystem() {
        if (deviceInfo == null) {
            return "Unknown";
        }
        if (deviceInfo.contains("Windows")) return "Windows";
        if (deviceInfo.contains("Mac")) return "MacOS";
        if (deviceInfo.contains("Linux")) return "Linux";
        if (deviceInfo.contains("Android")) return "Android";
        if (deviceInfo.contains("iOS")) return "iOS";
        return "Other";
    }

    /**
     * Verifica si es móvil
     */
    public boolean isMobile() {
        if (deviceInfo == null) {
            return false;
        }
        return deviceInfo.contains("Mobile") || 
               deviceInfo.contains("Android") || 
               deviceInfo.contains("iOS");
    }

    /**
     * Crea nuevo session token
     */
    public static SessionToken create(
            User user,
            String refreshTokenHash,
            String deviceInfo,
            String ipAddress,
            int expiryDays) {
        
        return SessionToken.builder()
                .user(user)
                .refreshTokenHash(refreshTokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusDays(expiryDays))
                .lastUsedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Descripción amigable de la sesión
     */
    public String getDescription() {
        String browser = getBrowser();
        String os = getOperatingSystem();
        String loc = location != null ? location : "Unknown location";
        return String.format("%s en %s desde %s", browser, os, loc);
    }
}
