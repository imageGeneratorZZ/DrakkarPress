package com.drakkarpress.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeedItem {
    private UUID id;
    private String type; // BOOK_PUBLISHED, STORY, REEL, POST
    private LocalDateTime timestamp;
    private UUID authorId;
    private String authorUsername;
    private String title;
    private String text;
    private String mediaUrl;
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private String relatedEntityType;
    private UUID relatedEntityId;
    private String safetyStatus; // Para libros / reels moderación
    
    // Ranking score (calculado por FeedRankingService)
    private Double rankingScore;
}
