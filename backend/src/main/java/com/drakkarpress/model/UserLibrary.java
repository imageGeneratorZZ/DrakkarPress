package com.drakkarpress.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_library")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column
    private Integer lastReadPosition; // Para ebooks: posición del texto

    @Column
    private Integer lastPageRead; // Para PDFs: número de página

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progress = BigDecimal.ZERO; // Porcentaje leído (0.00 - 100.00)

    @Column(columnDefinition = "TEXT")
    private String notes; // Notas del usuario

    @Column(columnDefinition = "JSONB")
    private String highlights; // JSON con highlights/subrayados

    @Column(nullable = false)
    @Builder.Default
    private Integer timesRead = 0;

    @Column
    private LocalDateTime lastReadAt;

    @Column
    private LocalDateTime finishedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Marca el libro como terminado
     */
    public void markAsFinished() {
        this.progress = new BigDecimal("100.00");
        this.finishedAt = LocalDateTime.now();
        this.timesRead++;
    }

    /**
     * Actualiza el progreso de lectura
     */
    public void updateProgress(int position, int total) {
        if (total > 0) {
            this.progress = new BigDecimal(position)
                .divide(new BigDecimal(total), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }
        this.lastReadAt = LocalDateTime.now();
    }

    /**
     * Verifica si el libro está terminado
     */
    public boolean isFinished() {
        return progress.compareTo(new BigDecimal("100.00")) == 0;
    }
}
