package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.dto.FeedItem;
import com.drakkarpress.platform.model.Reel;
import com.drakkarpress.platform.model.Story;
import com.drakkarpress.platform.model.UserActivityFeed;
import com.drakkarpress.platform.repository.ReelRepository;
import com.drakkarpress.platform.repository.StoryRepository;
import com.drakkarpress.repository.BookRepository;
import com.drakkarpress.platform.repository.UserActivityFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedAggregationService {

    private final UserActivityFeedRepository activityRepo;
    private final StoryRepository storyRepository;
    private final ReelRepository reelRepository;
    private final BookRepository bookRepository;

    public List<FeedItem> buildFeedForUser(UUID userId, int limit) {
        List<FeedItem> items = new ArrayList<>();

        // Actividades públicas recientes (limit 50)
        activityRepo.findByIsPublicTrueOrderByCreatedAtDesc().stream().limit(50).forEach(act -> {
            items.add(FeedItem.builder()
                    .id(act.getId())
                    .type(act.getActivityType())
                    .timestamp(act.getCreatedAt())
                    .authorId(act.getUser().getId())
                    .authorUsername(act.getUser().getUsername())
                    .title(act.getTitle())
                    .text(act.getContent())
                    .mediaUrl(act.getMediaUrl())
                    .likes(act.getLikesCount())
                    .comments(act.getCommentsCount())
                    .shares(act.getSharesCount())
                    .relatedEntityType(act.getRelatedEntityType())
                    .relatedEntityId(act.getRelatedEntityId())
                    .build());
        });

        // Stories activas propias (si se desea incluir globales, filtrar conexiones)
        storyRepository.findByUserIdAndExpiresAtAfter(userId, LocalDateTime.now()).forEach(story -> {
            items.add(FeedItem.builder()
                    .id(story.getId())
                    .type("STORY")
                    .timestamp(story.getCreatedAt())
                    .authorId(story.getUser().getId())
                    .authorUsername(story.getUser().getUsername())
                    .mediaUrl(story.getMediaUrl())
                    .text(story.getContentText())
                    .build());
        });

        // Reels recientes del usuario (placeholder: mostrar propios)
        reelRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().limit(20).forEach(reel -> {
            items.add(mapReel(reel));
        });

        // Libros publicados recientes (global) - fallback a orden creación si no hay método publishedAt
        bookRepository.findByStatusOrderByCreatedAtDesc(Book.BookStatus.PUBLISHED).stream().limit(20).forEach(book -> {
            items.add(FeedItem.builder()
                    .id(book.getId())
                    .type("BOOK_PUBLISHED")
                    .timestamp(book.getPublishedAt())
                    .authorId(book.getAuthor().getId())
                    .authorUsername(book.getAuthor().getUsername())
                    .title(book.getTitle())
                    .mediaUrl(book.getCoverImageUrl())
                    .relatedEntityType("BOOK")
                    .relatedEntityId(book.getId())
                    .safetyStatus(book.getSafetyStatus())
                    .build());
        });

        items.sort(Comparator.comparing(FeedItem::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())));
        if (items.size() > limit) {
            return items.subList(0, limit);
        }
        return items;
    }

    private FeedItem mapReel(Reel reel) {
        return FeedItem.builder()
                .id(reel.getId())
                .type("REEL")
                .timestamp(reel.getCreatedAt())
                .authorId(reel.getUser().getId())
                .authorUsername(reel.getUser().getUsername())
                .mediaUrl(reel.getMediaUrl())
                .likes(reel.getLikes())
                .comments(reel.getComments())
                .shares(reel.getShares())
                .build();
    }
}
