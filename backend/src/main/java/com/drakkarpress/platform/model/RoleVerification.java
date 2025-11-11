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
 * Entidad RoleVerification - Verificación de documentos para roles
 * 
 * Características principales:
 * - AUTHOR_PUBLISHER requiere: datos de pago (Stripe/PayPal)
 * - PRINT_SHOP requiere: certificación de calidad
 * - RESELLER requiere: datos de pago
 * - Estados: PENDING, APPROVED, REJECTED, EXPIRED
 */
@Entity
@Table(name = "role_verification", indexes = {
    @Index(name = "idx_role_verification_user_id", columnList = "user_id"),
    @Index(name = "idx_role_verification_role_type", columnList = "role_type"),
    @Index(name = "idx_role_verification_status", columnList = "verification_status"),
    @Index(name = "idx_role_verification_submitted_at", columnList = "submitted_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que solicita verificación
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tipo de rol a verificar
     * AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER
     */
    @Column(name = "role_type", nullable = false, length = 50)
    private String roleType;

    /**
     * Estado de la verificación
     * PENDING: en revisión
     * APPROVED: aprobado
     * REJECTED: rechazado
     * EXPIRED: venció (certificaciones con fecha de expiración)
     */
    @Column(name = "verification_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String verificationStatus;

    /**
     * Tipo de documento enviado
     * Ej: "PAYMENT_INFO", "CERTIFICATION", "TAX_ID", "BUSINESS_LICENSE", etc.
     */
    @Column(name = "document_type", nullable = false, length = 100)
    private String documentType;

    /**
     * URL del documento subido (S3, CloudStorage, etc.)
     */
    @Column(name = "document_url", length = 500)
    private String documentUrl;

    /**
     * Datos de verificación en formato JSON
     * Ej para AUTHOR_PUBLISHER: {"stripe_account_id": "acct_xxx", "payment_method": "stripe"}
     * Ej para PRINT_SHOP: {"certification_number": "CERT-12345", "expiry_date": "2026-12-31"}
     */
    @Column(name = "verification_data", columnDefinition = "JSONB")
    private String verificationData;

    /**
     * Notas adicionales del usuario
     */
    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;

    /**
     * Fecha de envío
     */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    /**
     * Fecha de revisión
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * Admin que revisó
     */
    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    /**
     * Notas del revisor
     */
    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    private String reviewerNotes;

    /**
     * Fecha de aprobación (si aplica)
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Fecha de rechazo (si aplica)
     */
    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    /**
     * Fecha de expiración (para certificaciones)
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

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
     * Verifica si está pendiente de revisión
     */
    public boolean isPending() {
        return "PENDING".equals(verificationStatus);
    }

    /**
     * Verifica si está aprobado
     */
    public boolean isApproved() {
        return "APPROVED".equals(verificationStatus);
    }

    /**
     * Verifica si está rechazado
     */
    public boolean isRejected() {
        return "REJECTED".equals(verificationStatus);
    }

    /**
     * Verifica si está expirado
     */
    public boolean isExpired() {
        if ("EXPIRED".equals(verificationStatus)) {
            return true;
        }
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Verifica si es verificación de pago
     */
    public boolean isPaymentVerification() {
        return "PAYMENT_INFO".equals(documentType);
    }

    /**
     * Verifica si es certificación
     */
    public boolean isCertification() {
        return "CERTIFICATION".equals(documentType);
    }

    /**
     * Aprueba la verificación
     */
    public void approve(UUID adminId, String notes) {
        this.verificationStatus = "APPROVED";
        this.approvedAt = LocalDateTime.now();
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = adminId;
        this.reviewerNotes = notes;
    }

    /**
     * Rechaza la verificación
     */
    public void reject(UUID adminId, String reason) {
        this.verificationStatus = "REJECTED";
        this.rejectedAt = LocalDateTime.now();
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = adminId;
        this.reviewerNotes = reason;
    }

    /**
     * Marca como expirado
     */
    public void expire() {
        this.verificationStatus = "EXPIRED";
    }

    /**
     * Días desde que se envió
     */
    public long getDaysSinceSubmission() {
        if (submittedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(submittedAt, LocalDateTime.now());
    }

    /**
     * Días hasta expiración (si aplica)
     */
    public long getDaysUntilExpiry() {
        if (expiresAt == null) {
            return -1; // Sin expiración
        }
        if (isExpired()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), expiresAt);
    }

    /**
     * Verifica si requiere renovación pronta (30 días antes de expirar)
     */
    public boolean needsRenewal() {
        if (expiresAt == null) {
            return false;
        }
        long daysUntilExpiry = getDaysUntilExpiry();
        return daysUntilExpiry >= 0 && daysUntilExpiry <= 30;
    }
}
