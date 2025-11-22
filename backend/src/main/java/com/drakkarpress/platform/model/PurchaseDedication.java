package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dedicatoria personalizada asociada a una compra de libro.
 * Se incrusta como página adicional en el EPUB generado sin modificar
 * el contenido original del autor.
 */
@Entity
@Table(name = "purchase_dedications", indexes = {
        @Index(name = "idx_purchase_dedications_purchase", columnList = "purchase_id"),
        @Index(name = "idx_purchase_dedications_hash", columnList = "hash", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseDedication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Compra asociada */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private BookPurchase purchase;

    /** Usuario que redacta la dedicatoria (comprador) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Texto original enviado por el comprador */
    @Column(name = "raw_message", length = 600)
    private String rawMessage;

    /** Texto sanitizado (filtros de abuso + recorte) */
    @Column(name = "sanitized_message", length = 500)
    private String sanitizedMessage;

    /** Hash verificable (SHA-256) para QR y endpoint público */
    @Column(name = "hash", length = 64, nullable = false, unique = true)
    private String hash;

    /** Indicador de si se insertó en EPUB dedicado */
    @Column(name = "injected", nullable = false)
    @Builder.Default
    private boolean injected = false;

    /** Ruta final del EPUB con dedicatoria (si procede) */
    @Column(name = "epub_path", columnDefinition = "TEXT")
    private String epubPath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Devuelve mensaje efectivo (sanitizado preferente) */
    public String getEffectiveMessage() {
        return sanitizedMessage != null ? sanitizedMessage : rawMessage;
    }
}
