package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.Story;
import com.drakkarpress.platform.repository.StoryRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryRepository storyRepository;
    private final PlatformUserRepository userRepository;

    @PostMapping
    public ApiResponse<Story> create(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId")); // TODO: derivar de auth
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Story.StoryType type = Story.StoryType.valueOf(body.get("type"));
        Story story = Story.builder()
                .user(user)
                .type(type)
                .contentText(body.getOrDefault("contentText", null))
                .mediaUrl(body.getOrDefault("mediaUrl", null))
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        storyRepository.save(story);
        return ApiResponse.ok("Story creada", story);
    }

    @GetMapping
    public ApiResponse<List<Story>> listActive(@RequestParam UUID userId) {
        return ApiResponse.ok(storyRepository.findByUserIdAndExpiresAtAfter(userId, LocalDateTime.now()));
    }
}
