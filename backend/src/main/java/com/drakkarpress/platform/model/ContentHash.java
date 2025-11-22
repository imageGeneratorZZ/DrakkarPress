package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Almacena hashes de contenido prohibido (PhotoDNA, MD5, SHA-256)
 * para matching rápido contra base de datos de contenido ilegal
 */
@Entity
@Table(name = "content_hashes", indexes = {
        @Index(name = "idx_hash_value", columnList = "hash_value", unique = true),
        @Index(name = "idx_hash_type", columnList = "hash_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentHash {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hash_value", nullable = false, unique = true, length = 128)
    private String hashValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "hash_type", nullable = false)
    private HashType hashType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentCategory category;

    @Column(columnDefinition = "TEXT")
    private String source; // NCMEC, INTERPOL, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public enum HashType {
        PHOTODNA,    // Microsoft PhotoDNA perceptual hash
        MD5,         // File hash
        SHA256,      // File hash
        PERCEPTUAL   // Generic perceptual hash
    }

    public enum ContentCategory {
        CSAM,           // Child Sexual Abuse Material
        TERRORISM,      // Terrorist content
        VIOLENCE,       // Extreme violence
        HATE_SPEECH,    // Hate speech
        SPAM            // Spam content
    }
}
