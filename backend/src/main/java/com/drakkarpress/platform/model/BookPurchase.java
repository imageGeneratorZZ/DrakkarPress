package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Compra de libros (ebooks digitales)
 * 
 * Registra compras de ebooks para descarga inmediata
 * Incluye envío automático por email y acceso desde biblioteca
 */
@Entity
@Table(name = "book_purchases", indexes = {
    @Index(name = "idx_book_purchases_user_id", columnList = "user_id"),
    @Index(name = "idx_book_purchases_book_id", columnList = "book_id"),
    @Index(name = "idx_book_purchases_transaction_id", columnList = "transaction_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Usuario que compró el libro
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Libro comprado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Transacción de pago relacionada
     */
    @Column(name = "transaction_id")
    private UUID transactionId;

    /**
     * Precio pagado
     */
    @Column(name = "price_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;

    /**
     * Moneda
     */
    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String currency;

    /**
     * Tipo de producto comprado
     * EBOOK_PDF, EBOOK_EPUB, EBOOK_BUNDLE, PHYSICAL_BOOK
     */
    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;

    /**
     * Estado de la compra
     * PENDING, COMPLETED, CANCELLED, REFUNDED
     */
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status;

    /**
     * Link de descarga (firmado, temporal)
     */
    @Column(name = "download_link", columnDefinition = "TEXT")
    private String downloadLink;

    /**
     * Fecha de expiración del link de descarga
     */
    @Column(name = "download_expires_at")
    private LocalDateTime downloadExpiresAt;

    /**
     * Número de descargas realizadas
     */
    @Column(name = "download_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer downloadCount;

    /**
     * Límite máximo de descargas (null = ilimitado)
     */
    @Column(name = "download_limit")
    private Integer downloadLimit;

    /**
     * Email enviado
     */
    @Column(name = "email_sent", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean emailSent;

    /**
     * Fecha de envío del email
     */
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    /**
     * Formato del archivo
     * PDF, EPUB, MOBI
     */
    @Column(name = "file_format", length = 10)
    private String fileFormat;

    /**
     * Ruta del archivo en S3/storage
     */
    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    /**
     * Tamaño del archivo en bytes
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Fecha de compra completada
     */
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    /**
     * Notas adicionales
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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
     * Verifica si está completada
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * Verifica si está pendiente
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Verifica si fue reembolsada
     */
    public boolean isRefunded() {
        return "REFUNDED".equals(status);
    }

    /**
     * Marca como completada
     */
    public void markCompleted() {
        this.status = "COMPLETED";
        this.purchasedAt = LocalDateTime.now();
    }

    /**
     * Marca email como enviado
     */
    public void markEmailSent() {
        this.emailSent = true;
        this.emailSentAt = LocalDateTime.now();
    }

    /**
     * Incrementa contador de descargas
     */
    public void incrementDownloadCount() {
        if (this.downloadCount == null) {
            this.downloadCount = 0;
        }
        this.downloadCount++;
    }

    /**
     * Verifica si puede descargar más veces
     */
    public boolean canDownload() {
        if (downloadLimit == null) {
            return true; // Ilimitado
        }
        return downloadCount == null || downloadCount < downloadLimit;
    }

    /**
     * Verifica si el link de descarga expiró
     */
    public boolean isDownloadLinkExpired() {
        if (downloadExpiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(downloadExpiresAt);
    }

    /**
     * Genera nuevo link de descarga con expiración
     */
    public void generateDownloadLink(String baseUrl, int hoursValid) {
        String token = UUID.randomUUID().toString();
        this.downloadLink = baseUrl + "/api/downloads/" + this.id + "?token=" + token;
        this.downloadExpiresAt = LocalDateTime.now().plusHours(hoursValid);
    }

    /**
     * Verifica si es ebook digital
     */
    public boolean isDigitalEbook() {
        return productType != null && productType.startsWith("EBOOK_");
    }

    /**
     * Verifica si es libro físico
     */
    public boolean isPhysicalBook() {
        return "PHYSICAL_BOOK".equals(productType);
    }

    /**
     * Obtiene nombre del formato
     */
    public String getFormatName() {
        if (fileFormat == null) {
            return "Unknown";
        }
        return switch (fileFormat) {
            case "PDF" -> "PDF Digital";
            case "EPUB" -> "EPUB (eReaders)";
            case "MOBI" -> "MOBI (Kindle)";
            default -> fileFormat;
        };
    }

    /**
     * Formatea el precio
     */
    public String getFormattedPrice() {
        if (pricePaid == null) {
            return "$0.00";
        }
        return String.format("$%.2f %s", pricePaid, currency);
    }

    /**
     * Obtiene tamaño formateado
     */
    public String getFormattedSize() {
        if (fileSize == null || fileSize == 0) {
            return "0 KB";
        }
        double kb = fileSize / 1024.0;
        if (kb < 1024) {
            return String.format("%.1f KB", kb);
        }
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }

    /**
     * Crea compra de ebook
     */
    public static BookPurchase createEbookPurchase(
            User user,
            Book book,
            BigDecimal price,
            String format,
            UUID transactionId) {
        
        return BookPurchase.builder()
                .user(user)
                .book(book)
                .transactionId(transactionId)
                .pricePaid(price)
                .currency("USD")
                .productType("EBOOK_" + format.toUpperCase())
                .fileFormat(format.toUpperCase())
                .status("PENDING")
                .downloadCount(0)
                .downloadLimit(10) // Límite por defecto
                .emailSent(false)
                .build();
    }
}
