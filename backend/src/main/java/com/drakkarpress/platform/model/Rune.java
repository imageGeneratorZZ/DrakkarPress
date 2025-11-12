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
 * Entity representing the runes from Elder Futhark.
 * Used for Premium user personalization.
 */
@Entity
@Table(name = "runes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rune {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol; // ej: 'ᚲ'

    @Column(nullable = false, unique = true, length = 50)
    private String name; // ej: 'Kenaz'

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaningEs;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaningEn;

    @Column(nullable = false, length = 50)
    private String category; // 'CREATIVITY_KNOWLEDGE', 'SUCCESS_ACHIEVEMENT', etc.

    @Column(columnDefinition = "TEXT")
    private String descriptionEs;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(nullable = false)
    @Builder.Default
    private Integer timesSelected = 0;

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
    @OneToMany(mappedBy = "rune", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRune> userRunes = new HashSet<>();

    @OneToMany(mappedBy = "rune", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Badge> badges = new HashSet<>();
}
