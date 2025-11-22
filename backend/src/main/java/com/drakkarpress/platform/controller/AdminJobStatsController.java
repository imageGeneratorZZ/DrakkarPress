package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.security.JwtUserPrincipal;
import com.drakkarpress.platform.service.JobStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobStatsController {
    private final JobStatsService statsService;

    @GetMapping("/stats")
    public ApiResponse<?> stats(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal) || (principal.role() == null || !principal.role().equalsIgnoreCase("ADMIN"))) {
            return ApiResponse.error("No autorizado");
        }
        return ApiResponse.ok(statsService.getStats());
    }
}
