package com.drakkarpress.platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "moderation_flags", indexes = {
        @Index(name = "idx_mod_flags_resource", columnList = "resourceType, resourceId"),
        @Index(name = "idx_mod_flags_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationFlag {

    public enum ResourceType { BOOK, STORY, REEL, PROFILE_IMG }
    public enum Status { PENDING, APPROVED, REJECTED, ESCALATED, AUTO_BLOCKED }
    public enum FinalDecision { SAFE, BLOCKED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResourceType resourceType;

    @Column(nullable = false)
    private UUID resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FinalDecision finalDecision;

    @Column(columnDefinition = "TEXT")
    private String scoresJson; // JSON con scores (sexual_explicit, minor_risk, etc.)

    @Column(columnDefinition = "TEXT")
    private String reviewerNotes; // Notas del revisor humano

    @Column(length = 200)
    private String escalatedTo; // Autoridad externa si aplica

    @Column
    private UUID reviewerId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
