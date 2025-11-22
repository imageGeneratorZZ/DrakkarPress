package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.repository.SaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/reseller/dashboard")
public class ResellerDashboardController {
    private final SaleRepository saleRepository;

    public ResellerDashboardController(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RESELLER') or hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> dashboard(org.springframework.security.core.Authentication auth) {
        UUID userId;
        try {
            userId = ((com.drakkarpress.platform.model.User) auth.getPrincipal()).getId();
        } catch (ClassCastException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("Principal inválido"));
        }
        BigDecimal revenue = saleRepository.totalRevenueForReseller(userId);
        Map<String,Object> payload = new HashMap<>();
        payload.put("revenue", revenue);
        return ResponseEntity.ok(ApiResponse.success("OK", payload));
    }
}
