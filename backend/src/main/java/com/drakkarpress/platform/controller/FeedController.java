package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.dto.FeedItem;
import com.drakkarpress.platform.service.FeedAggregationService;
import com.drakkarpress.platform.service.FeedRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedAggregationService feedAggregationService;
    private final FeedRankingService feedRankingService;

    /**
     * Feed básico (orden cronológico simple)
     */
    @GetMapping
    public ApiResponse<List<FeedItem>> getFeed(Authentication authentication,
                                               @RequestParam(defaultValue = "50") int limit) {
        if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ApiResponse.error("Usuario no autenticado");
        }
        UUID userId = principal.userId();
        return ApiResponse.ok(feedAggregationService.buildFeedForUser(userId, limit));
    }

    /**
     * Feed personalizado con ranking inteligente
     * (engagement + recency + connections + safety)
     */
    @GetMapping("/personalized")
    public ApiResponse<List<FeedItem>> getPersonalizedFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "50") int limit) {
        if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ApiResponse.error("Usuario no autenticado");
        }
        UUID userId = principal.userId();
        List<FeedItem> rankedFeed = feedRankingService.buildPersonalizedFeed(userId, limit);
        return ApiResponse.ok(rankedFeed);
    }
}
