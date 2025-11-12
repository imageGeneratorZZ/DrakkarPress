package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing system badges that can be assigned to users.
 */
@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // 'FOUNDER', 'EARLY_ADOPTER', 'PREMIUM', etc.

    @Column(nullable = false, length = 100)
    private String nameEs;

    @Column(nullable = false, length = 100)
    private String nameEn;

    @Column(length = 50)
    private String icon; // '🏆', '⭐', '✨', etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rune_id")
    private Rune rune; // Runa asociada al badge (opcional)

    @Column(length = 7)
    private String colorHex; // Color del badge en hex

    @Column(columnDefinition = "TEXT")
    private String descriptionEs;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoAssign = false; // Se asigna automáticamente

    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresPremium = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Integer displayOrder;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserBadge> userBadges = new HashSet<>();
}
