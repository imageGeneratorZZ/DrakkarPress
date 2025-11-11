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
 * Entidad Message - Mensajería interna entre usuarios
 * 
 * Características principales:
 * - DM (Direct Messages) entre usuarios
 * - Estados: enviado, leído, archivado
 * - Notificaciones de lectura
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_messages_sender_id", columnList = "sender_id"),
    @Index(name = "idx_messages_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_messages_is_read", columnList = "is_read"),
    @Index(name = "idx_messages_sent_at", columnList = "sent_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que envía el mensaje
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Usuario que recibe el mensaje
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    /**
     * Asunto del mensaje (opcional)
     */
    @Column(name = "subject", length = 255)
    private String subject;

    /**
     * Contenido del mensaje
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * URL de archivos adjuntos (si hay)
     */
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    /**
     * Si el mensaje fue leído
     */
    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead;

    /**
     * Fecha de lectura
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Si está archivado por el sender
     */
    @Column(name = "archived_by_sender", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean archivedBySender;

    /**
     * Si está archivado por el recipient
     */
    @Column(name = "archived_by_recipient", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean archivedByRecipient;

    /**
     * Si está eliminado por el sender
     */
    @Column(name = "deleted_by_sender", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deletedBySender;

    /**
     * Si está eliminado por el recipient
     */
    @Column(name = "deleted_by_recipient", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deletedByRecipient;

    /**
     * ID del mensaje al que responde (threading)
     */
    @Column(name = "reply_to_message_id")
    private UUID replyToMessageId;

    /**
     * Metadata adicional en JSON
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Marca como leído
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Marca como no leído
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * Archiva para el sender
     */
    public void archiveBySender() {
        this.archivedBySender = true;
    }

    /**
     * Archiva para el recipient
     */
    public void archiveByRecipient() {
        this.archivedByRecipient = true;
    }

    /**
     * Desarchivar para el sender
     */
    public void unarchiveBySender() {
        this.archivedBySender = false;
    }

    /**
     * Desarchivar para el recipient
     */
    public void unarchiveByRecipient() {
        this.archivedByRecipient = false;
    }

    /**
     * Elimina para el sender
     */
    public void deleteBySender() {
        this.deletedBySender = true;
    }

    /**
     * Elimina para el recipient
     */
    public void deleteByRecipient() {
        this.deletedByRecipient = true;
    }

    /**
     * Verifica si está completamente eliminado (por ambos)
     */
    public boolean isFullyDeleted() {
        return deletedBySender && deletedByRecipient;
    }

    /**
     * Verifica si es una respuesta
     */
    public boolean isReply() {
        return replyToMessageId != null;
    }

    /**
     * Verifica si tiene adjuntos
     */
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.isEmpty();
    }

    /**
     * Tiempo desde el envío
     */
    public long getMinutesSinceSent() {
        if (sentAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(sentAt, LocalDateTime.now());
    }

    /**
     * Verifica si es reciente (menos de 1 hora)
     */
    public boolean isRecent() {
        return getMinutesSinceSent() <= 60;
    }

    /**
     * Tiempo de lectura (desde envío hasta lectura)
     */
    public long getMinutesToRead() {
        if (sentAt == null || readAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(sentAt, readAt);
    }

    /**
     * Obtiene username del sender
     */
    public String getSenderUsername() {
        return sender != null ? sender.getUsername() : null;
    }

    /**
     * Obtiene username del recipient
     */
    public String getRecipientUsername() {
        return recipient != null ? recipient.getUsername() : null;
    }

    /**
     * Verifica si el usuario es el sender
     */
    public boolean isSender(UUID userId) {
        return sender != null && sender.getId().equals(userId);
    }

    /**
     * Verifica si el usuario es el recipient
     */
    public boolean isRecipient(UUID userId) {
        return recipient != null && recipient.getId().equals(userId);
    }

    /**
     * Verifica si el mensaje está visible para el usuario
     */
    public boolean isVisibleFor(UUID userId) {
        if (isSender(userId)) {
            return !deletedBySender;
        }
        if (isRecipient(userId)) {
            return !deletedByRecipient;
        }
        return false;
    }

    /**
     * Preview del contenido (primeros 100 caracteres)
     */
    public String getContentPreview() {
        if (content == null) {
            return "";
        }
        if (content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    /**
     * Crea un nuevo mensaje
     */
    public static Message create(
            User sender,
            User recipient,
            String subject,
            String content) {
        
        return Message.builder()
                .sender(sender)
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .isRead(false)
                .archivedBySender(false)
                .archivedByRecipient(false)
                .deletedBySender(false)
                .deletedByRecipient(false)
                .build();
    }

    /**
     * Crea una respuesta a otro mensaje
     */
    public static Message createReply(
            User sender,
            User recipient,
            String content,
            UUID replyToMessageId) {
        
        return Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .replyToMessageId(replyToMessageId)
                .isRead(false)
                .archivedBySender(false)
                .archivedByRecipient(false)
                .deletedBySender(false)
                .deletedByRecipient(false)
                .build();
    }
}
