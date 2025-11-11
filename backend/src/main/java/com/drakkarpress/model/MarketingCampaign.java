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
@Table(name = "marketing_campaigns")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketingCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(length = 255)
    private String campaignName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.PENDING;

    // Métricas de la campaña
    @Column
    private Integer impressions;

    @Column
    private Integer clicks;

    @Column
    private Integer conversions;

    @Column(precision = 5, scale = 2)
    private BigDecimal ctr; // Click-Through Rate

    @Column(precision = 5, scale = 2)
    private BigDecimal conversionRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal revenue;

    @Column(precision = 5, scale = 2)
    private BigDecimal roi; // Return on Investment

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ServiceType {
        LAUNCH_PACKAGE("Paquete Lanzamiento", new BigDecimal("2999.00")),
        FACEBOOK_ADS("Facebook Ads", new BigDecimal("999.00")),
        GOOGLE_ADS("Google Ads", new BigDecimal("899.00")),
        AMAZON_AMS("Amazon Marketing Services", new BigDecimal("799.00")),
        COVER_DESIGN("Diseño de Portada", new BigDecimal("299.00")),
        AUTHOR_BRANDING("Branding de Autor", new BigDecimal("1999.00")),
        BOOKBUB_DEAL("BookBub Featured Deal", new BigDecimal("499.00")),
        EMAIL_MARKETING("Email Marketing", new BigDecimal("399.00")),
        LANDING_PAGE("Landing Page", new BigDecimal("599.00")),
        SEO_OPTIMIZATION("Optimización SEO", new BigDecimal("299.00"));

        private final String displayName;
        private final BigDecimal basePrice;

        ServiceType(String displayName, BigDecimal basePrice) {
            this.displayName = displayName;
            this.basePrice = basePrice;
        }

        public String getDisplayName() {
            return displayName;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }
    }

    public enum CampaignStatus {
        PENDING,        // Pendiente de inicio
        ACTIVE,         // En ejecución
        PAUSED,         // Pausada
        COMPLETED,      // Completada
        CANCELLED       // Cancelada
    }

    /**
     * Calcula el ROI de la campaña
     */
    public void calculateROI() {
        if (budget != null && budget.compareTo(BigDecimal.ZERO) > 0 && revenue != null) {
            BigDecimal profit = revenue.subtract(budget);
            this.roi = profit.divide(budget, 2, java.math.RoundingMode.HALF_UP)
                             .multiply(new BigDecimal("100"));
        }
    }

    /**
     * Calcula el CTR (Click-Through Rate)
     */
    public void calculateCTR() {
        if (impressions != null && impressions > 0 && clicks != null) {
            this.ctr = new BigDecimal(clicks)
                .divide(new BigDecimal(impressions), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }
    }

    /**
     * Calcula la tasa de conversión
     */
    public void calculateConversionRate() {
        if (clicks != null && clicks > 0 && conversions != null) {
            this.conversionRate = new BigDecimal(conversions)
                .divide(new BigDecimal(clicks), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }
    }
}
