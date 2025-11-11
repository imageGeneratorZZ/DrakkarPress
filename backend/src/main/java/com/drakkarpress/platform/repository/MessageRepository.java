package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Mensajes recibidos no eliminados
     */
    @Query("SELECT m FROM Message m WHERE m.recipient.id = :userId AND m.deletedByRecipient = false ORDER BY m.sentAt DESC")
    List<Message> findReceivedMessages(@Param("userId") UUID userId);

    /**
     * Mensajes enviados no eliminados
     */
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId AND m.deletedBySender = false ORDER BY m.sentAt DESC")
    List<Message> findSentMessages(@Param("userId") UUID userId);

    /**
     * Mensajes no leídos
     */
    List<Message> findByRecipientIdAndIsReadFalseAndDeletedByRecipientFalseOrderBySentAtDesc(UUID recipientId);

    /**
     * Contar mensajes no leídos
     */
    long countByRecipientIdAndIsReadFalseAndDeletedByRecipientFalse(UUID recipientId);

    /**
     * Conversación entre dos usuarios
     */
    @Query("SELECT m FROM Message m WHERE ((m.sender.id = :user1 AND m.recipient.id = :user2 AND m.deletedBySender = false) OR (m.sender.id = :user2 AND m.recipient.id = :user1 AND m.deletedByRecipient = false)) ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user1") UUID user1, @Param("user2") UUID user2);

    /**
     * Mensajes archivados
     */
    @Query("SELECT m FROM Message m WHERE m.recipient.id = :userId AND m.archivedByRecipient = true AND m.deletedByRecipient = false ORDER BY m.sentAt DESC")
    List<Message> findArchivedMessages(@Param("userId") UUID userId);

    /**
     * Respuestas a un mensaje
     */
    List<Message> findByReplyToMessageIdOrderBySentAtAsc(UUID replyToMessageId);

    /**
     * Mensajes completamente eliminados (limpieza)
     */
    @Query("SELECT m FROM Message m WHERE m.deletedBySender = true AND m.deletedByRecipient = true")
    List<Message> findFullyDeleted();
}
