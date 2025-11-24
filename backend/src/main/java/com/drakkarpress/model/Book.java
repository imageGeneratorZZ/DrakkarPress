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
import java.util.*;

@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Column(length = 20)
    private String isbn;

    @Column(length = 100)
    private String language; // ES, EN, FR, DE, etc.

    @Column(nullable = false)
    private Integer pages;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceDigital;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePhysical;

    @Column(length = 500)
    private String coverImageUrl;

    @Column(length = 500)
    private String digitalFileUrl; // EPUB/PDF en S3

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookStatus status = BookStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aiGenerated = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer views = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer downloads = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer sales = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer commentsCount = 0;

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column
    private Integer reviewsCount;

    @Column(columnDefinition = "TEXT")
    private String keywords; // Para SEO

    @Column
    private LocalDateTime publishedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Integraciones externas / distribución
    @Column(length = 100)
    private String kdpExternalId;

    @Column(length = 100)
    private String googlePlayExternalId;

    @Column(length = 100)
    private String luluExternalId;

    // Estado de seguridad / moderación (SAFE, REVIEW, BLOCKED)
    @Column(length = 20)
    @Builder.Default
    private String safetyStatus = "UNKNOWN";

    // Relationships
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Sale> salesList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserLibrary> inLibraries = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MarketingCampaign> campaigns = new ArrayList<>();

    // Business Methods
    public void incrementViews() {
        this.views++;
    }

    public void incrementDownloads() {
        this.downloads++;
    }

    public void incrementSales() {
        this.sales++;
    }

    public void incrementLikes() { this.likes++; }
    public void decrementLikes() { if (this.likes > 0) this.likes--; }
    public void incrementCommentsCount() { this.commentsCount++; }
    public void decrementCommentsCount() { if (this.commentsCount > 0) this.commentsCount--; }

    public boolean isPublished() {
        return status == BookStatus.PUBLISHED;
    }

    public enum Genre {
        // Ficción
        ROMANCE("Romance"),
        EROTICA("Erótica"),
        THRILLER("Thriller/Suspenso"),
        MYSTERY("Misterio"),
        FANTASY("Fantasía"),
        SCIFI("Ciencia Ficción"),
        HORROR("Horror"),
        HISTORICAL_FICTION("Ficción Histórica"),
        CONTEMPORARY("Contemporánea"),
        
        // No Ficción
        SELF_HELP("Desarrollo Personal"),
        BUSINESS("Negocios"),
        COOKING("Cocina"),
        BIOGRAPHY("Biografía"),
        HISTORY("Historia"),
        SCIENCE("Ciencia"),
        TRAVEL("Viajes"),
        HEALTH("Salud"),
        
        // Infantil
        CHILDREN("Infantil"),
        COLORING("Colorear"),
        YOUNG_ADULT("Jóvenes Adultos"),
        
        // Otros
        POETRY("Poesía"),
        RELIGION("Religión"),
        OTHER("Otro");

        private final String displayName;

        Genre(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum BookStatus {
        DRAFT,          // Borrador
        IN_REVIEW,      // En revisión
        PUBLISHED,      // Publicado
        ARCHIVED,       // Archivado
        SUSPENDED       // Suspendido
    }
}
