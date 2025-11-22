package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.repository.SaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/author/dashboard")
public class AuthorDashboardController {
    private final SaleRepository saleRepository;

    public AuthorDashboardController(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AUTHOR') or hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> dashboard(org.springframework.security.core.Authentication auth) {
        // Placeholder: principal type simplificado; adaptar a wrapper de seguridad real
        UUID userId;
        try {
            userId = ((com.drakkarpress.platform.model.User) auth.getPrincipal()).getId();
        } catch (ClassCastException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("Principal inválido"));
        }
        BigDecimal revenue = saleRepository.totalRevenueForAuthor(userId);
        long salesLast30 = saleRepository.countSalesSince(LocalDateTime.now().minusDays(30));
        Map<String,Object> payload = new HashMap<>();
        payload.put("revenue", revenue);
        payload.put("salesLast30Days", salesLast30);
        return ResponseEntity.ok(ApiResponse.success("OK", payload));
    }
}
