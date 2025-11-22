package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.Reel;
import com.drakkarpress.platform.repository.ReelRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ReelController {

    private final ReelRepository reelRepository;
    private final PlatformUserRepository userRepository;

    @PostMapping
    public ApiResponse<Reel> create(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId")); // TODO: auth principal
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String mediaUrl = body.get("mediaUrl");
        String caption = body.getOrDefault("caption", null);
        Integer duration = Integer.parseInt(body.getOrDefault("durationSeconds", "0"));
        Reel reel = Reel.builder()
                .user(user)
                .mediaUrl(mediaUrl)
                .caption(caption)
                .durationSeconds(duration)
                .likes(0)
                .comments(0)
                .shares(0)
                .build();
        reelRepository.save(reel);
        return ApiResponse.ok("Reel creado", reel);
    }

    @GetMapping
    public ApiResponse<List<Reel>> list(@RequestParam UUID userId) {
        return ApiResponse.ok(reelRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Reel> like(@PathVariable UUID id) {
        Reel reel = reelRepository.findById(id).orElseThrow(() -> new RuntimeException("Reel no encontrado"));
        reel.incrementLikes();
        reelRepository.save(reel);
        return ApiResponse.ok("Like reel", reel);
    }

    @PostMapping("/{id}/unlike")
    public ApiResponse<Reel> unlike(@PathVariable UUID id) {
        Reel reel = reelRepository.findById(id).orElseThrow(() -> new RuntimeException("Reel no encontrado"));
        reel.decrementLikes();
        reelRepository.save(reel);
        return ApiResponse.ok("Unlike reel", reel);
    }

    @PostMapping("/{id}/comment")
    public ApiResponse<Map<String, Object>> comment(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        Reel reel = reelRepository.findById(id).orElseThrow(() -> new RuntimeException("Reel no encontrado"));
        reel.incrementComments();
        reelRepository.save(reel);
        return ApiResponse.ok(Map.of(
                "reelId", reel.getId(),
                "comments", reel.getComments(),
                "comment", body.getOrDefault("text", "")
        ));
    }
}
