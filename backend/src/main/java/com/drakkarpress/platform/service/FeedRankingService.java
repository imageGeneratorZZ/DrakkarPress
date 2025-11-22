package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.dto.FeedItem;
import com.drakkarpress.platform.model.Reel;
import com.drakkarpress.platform.model.Story;
import com.drakkarpress.platform.model.UserActivityFeed;
import com.drakkarpress.platform.repository.ConnectionRepository;
import com.drakkarpress.platform.repository.ReelRepository;
import com.drakkarpress.platform.repository.StoryRepository;
import com.drakkarpress.platform.repository.UserActivityFeedRepository;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de ranking personalizado del feed
 * Algoritmo: Engagement Score × Time Decay × Connection Relevance × Safety Filter
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedRankingService {

    private final UserActivityFeedRepository activityRepo;
    private final StoryRepository storyRepository;
    private final ReelRepository reelRepository;
    private final BookRepository bookRepository;
    private final ConnectionRepository connectionRepository;

    // Parámetros de ranking
    private static final double ENGAGEMENT_WEIGHT = 0.4;
    private static final double RECENCY_WEIGHT = 0.3;
    private static final double CONNECTION_WEIGHT = 0.2;
    private static final double SAFETY_WEIGHT = 0.1;
    
    // Time decay: half-life de 24 horas
    private static final double TIME_DECAY_HALF_LIFE_HOURS = 24.0;

    /**
     * Construye feed personalizado con ranking inteligente
     */
    public List<FeedItem> buildPersonalizedFeed(UUID userId, int limit) {
        log.debug("Building personalized feed for user {}", userId);

        // 1. Obtener conexiones del usuario (following)
        List<UUID> followingIds = connectionRepository.findByFollowerIdAndConnectionStatusOrderByCreatedAtDesc(userId, "ACCEPTED")
                .stream()
                .map(conn -> conn.getFollowed().getId())
                .collect(Collectors.toList());

        // 2. Recolectar todos los items candidatos
        List<FeedItem> candidates = new ArrayList<>();
        
        // Actividades públicas recientes
        activityRepo.findByIsPublicTrueOrderByCreatedAtDesc().stream()
                .limit(100)
                .forEach(act -> candidates.add(mapActivity(act, followingIds)));

        // Stories activas (solo de conexiones + propias)
        List<UUID> storyAuthors = new ArrayList<>(followingIds);
        storyAuthors.add(userId);
        storyAuthors.forEach(authorId -> {
            storyRepository.findByUserIdAndExpiresAtAfter(authorId, LocalDateTime.now())
                    .forEach(story -> candidates.add(mapStory(story, followingIds)));
        });

        // Reels recientes (conexiones + propios + trending)
        reelRepository.findAll().stream()
                .sorted(Comparator.comparing(Reel::getCreatedAt).reversed())
                .limit(100)
                .forEach(reel -> candidates.add(mapReel(reel, followingIds)));

        // Libros publicados (conexiones + trending)
        bookRepository.findByStatusOrderByCreatedAtDesc(Book.BookStatus.PUBLISHED).stream()
                .limit(100)
                .forEach(book -> candidates.add(mapBook(book, followingIds)));

        // 3. Aplicar filtro de seguridad (eliminar BLOCKED)
        List<FeedItem> filteredCandidates = candidates.stream()
                .filter(item -> !"BLOCKED".equals(item.getSafetyStatus()))
                .collect(Collectors.toList());

        // 4. Calcular scores y ordenar
        LocalDateTime now = LocalDateTime.now();
        filteredCandidates.forEach(item -> {
            double score = calculateRankingScore(item, now, followingIds);
            item.setRankingScore(score);
        });

        filteredCandidates.sort(Comparator.comparing(FeedItem::getRankingScore).reversed());

        // 5. Limitar resultados
        return filteredCandidates.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Calcula score de ranking combinando engagement, recency, conexiones y safety
     */
    private double calculateRankingScore(FeedItem item, LocalDateTime now, List<UUID> followingIds) {
        // Engagement score (normalizado 0-1)
        double engagementScore = calculateEngagementScore(item);
        
        // Time decay (exponencial)
        double recencyScore = calculateTimeDecay(item.getTimestamp(), now);
        
        // Connection relevance (1.0 si es de alguien que sigues, 0.3 si no)
        double connectionScore = followingIds.contains(item.getAuthorId()) ? 1.0 : 0.3;
        
        // Safety score (1.0 SAFE, 0.5 REVIEW, 0.0 BLOCKED)
        double safetyScore = calculateSafetyScore(item.getSafetyStatus());

        // Combinación ponderada
        double finalScore = 
            (engagementScore * ENGAGEMENT_WEIGHT) +
            (recencyScore * RECENCY_WEIGHT) +
            (connectionScore * CONNECTION_WEIGHT) +
            (safetyScore * SAFETY_WEIGHT);

        return finalScore;
    }

    /**
     * Engagement score basado en likes, comments, shares
     * Normalizado usando función logarítmica para evitar sesgos extremos
     */
    private double calculateEngagementScore(FeedItem item) {
        int likes = item.getLikes() != null ? item.getLikes() : 0;
        int comments = item.getComments() != null ? item.getComments() : 0;
        int shares = item.getShares() != null ? item.getShares() : 0;

        // Ponderación: comments valen más que likes, shares valen más que comments
        double totalEngagement = likes + (comments * 3) + (shares * 5);

        // Normalización logarítmica (evita que posts virales dominen todo)
        double normalized = Math.log1p(totalEngagement) / Math.log1p(10000); // max ~10k engagement

        return Math.min(normalized, 1.0);
    }

    /**
     * Time decay exponencial (half-life de 24 horas)
     */
    private double calculateTimeDecay(LocalDateTime timestamp, LocalDateTime now) {
        if (timestamp == null) return 0.0;

        long hoursAgo = Duration.between(timestamp, now).toHours();
        
        // Exponential decay: score = 0.5^(hours / half_life)
        double decay = Math.pow(0.5, hoursAgo / TIME_DECAY_HALF_LIFE_HOURS);
        
        return Math.max(decay, 0.001); // Mínimo 0.1% para contenido antiguo pero relevante
    }

    /**
     * Safety score
     */
    private double calculateSafetyScore(String safetyStatus) {
        if (safetyStatus == null || "SAFE".equals(safetyStatus) || "UNKNOWN".equals(safetyStatus)) {
            return 1.0;
        }
        if ("REVIEW".equals(safetyStatus)) {
            return 0.5; // Penalizar ligeramente contenido en revisión
        }
        return 0.0; // BLOCKED no debería llegar aquí (filtrado previamente)
    }

    // Mappers

    private FeedItem mapActivity(UserActivityFeed activity, List<UUID> followingIds) {
        return FeedItem.builder()
                .id(activity.getId())
                .type(activity.getActivityType())
                .timestamp(activity.getCreatedAt())
                .authorId(activity.getUser().getId())
                .authorUsername(activity.getUser().getUsername())
                .title(activity.getTitle())
                .text(activity.getContent())
                .mediaUrl(activity.getMediaUrl())
                .likes(activity.getLikesCount())
                .comments(activity.getCommentsCount())
                .shares(activity.getSharesCount())
                .relatedEntityType(activity.getRelatedEntityType())
                .relatedEntityId(activity.getRelatedEntityId())
                .safetyStatus("SAFE") // Activities no tienen safetyStatus aún
                .build();
    }

    private FeedItem mapStory(Story story, List<UUID> followingIds) {
        return FeedItem.builder()
                .id(story.getId())
                .type("STORY")
                .timestamp(story.getCreatedAt())
                .authorId(story.getUser().getId())
                .authorUsername(story.getUser().getUsername())
                .mediaUrl(story.getMediaUrl())
                .text(story.getContentText())
                .safetyStatus("SAFE") // Stories no tienen safetyStatus aún
                .build();
    }

    private FeedItem mapReel(Reel reel, List<UUID> followingIds) {
        return FeedItem.builder()
                .id(reel.getId())
                .type("REEL")
                .timestamp(reel.getCreatedAt())
                .authorId(reel.getUser().getId())
                .authorUsername(reel.getUser().getUsername())
                .mediaUrl(reel.getMediaUrl())
                .text(reel.getCaption())
                .likes(reel.getLikes())
                .comments(reel.getComments())
                .shares(reel.getShares())
                .safetyStatus("SAFE") // Reels no tienen safetyStatus aún
                .build();
    }

    private FeedItem mapBook(Book book, List<UUID> followingIds) {
        return FeedItem.builder()
                .id(book.getId())
                .type("BOOK_PUBLISHED")
                .timestamp(book.getPublishedAt() != null ? book.getPublishedAt() : book.getCreatedAt())
                .authorId(book.getAuthor().getId())
                .authorUsername(book.getAuthor().getUsername())
                .title(book.getTitle())
                .text(book.getSynopsis())
                .mediaUrl(book.getCoverImageUrl())
                .likes(book.getLikes())
                .comments(book.getCommentsCount())
                .relatedEntityType("BOOK")
                .relatedEntityId(book.getId())
                .safetyStatus(book.getSafetyStatus())
                .build();
    }
}
