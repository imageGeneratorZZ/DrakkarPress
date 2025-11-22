package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.ModerationFlag;
import com.drakkarpress.platform.repository.ModerationFlagRepository;
import com.drakkarpress.platform.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ModerationController {

    private final ModerationFlagRepository moderationFlagRepository;
    private final ModerationService moderationService;

    @GetMapping("/flags")
    public ApiResponse<List<ModerationFlag>> listFlags(@RequestParam(required = false) String status) {
        if (status != null) {
            return ApiResponse.ok(moderationFlagRepository.findByStatus(ModerationFlag.Status.valueOf(status)));
        }
        return ApiResponse.ok(moderationFlagRepository.findAll());
    }

    @PostMapping("/flags/{id}/decision")
    public ApiResponse<ModerationFlag> decide(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        ModerationFlag flag = moderationFlagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flag no encontrado"));
        String decision = body.get("decision");
        flag.setFinalDecision(ModerationFlag.FinalDecision.valueOf(decision));
        flag.setStatus(decision.equals("SAFE") ? ModerationFlag.Status.APPROVED : ModerationFlag.Status.REJECTED);
        moderationFlagRepository.save(flag);
        return ApiResponse.ok("Decisión aplicada", flag);
    }

    @PostMapping("/analyze")
    public ApiResponse<ModerationFlag> analyze(@RequestBody Map<String, String> body) {
        ModerationFlag.ResourceType type = ModerationFlag.ResourceType.valueOf(body.get("resourceType"));
        UUID resourceId = UUID.fromString(body.get("resourceId"));
        ModerationFlag flag = moderationService.analyzeAndFlag(type, resourceId, body.get("text"), body.get("mediaUrl"), null);
        return ApiResponse.ok(flag);
    }
}
