package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/print-shop/dashboard")
public class PrintShopDashboardController {

    @GetMapping
    @PreAuthorize("hasAuthority('PRINT_SHOP') or hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> dashboard() {
        Map<String,Object> payload = new HashMap<>();
        payload.put("pendingJobs", 0);
        payload.put("completedJobs", 0);
        payload.put("avgTurnaroundDays", 0);
        return ResponseEntity.ok(ApiResponse.success("OK", payload));
    }
}
