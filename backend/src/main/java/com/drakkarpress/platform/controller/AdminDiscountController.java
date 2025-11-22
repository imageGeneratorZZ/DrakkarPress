package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.model.DiscountRule;
import com.drakkarpress.platform.repository.DiscountRuleRepository;
import com.drakkarpress.platform.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/discount-rules")
public class AdminDiscountController {

    private final DiscountRuleRepository repository;

    public AdminDiscountController(DiscountRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<DiscountRule>>> list(@RequestParam(required = false) String type) {
        List<DiscountRule> rules = (type == null) ? repository.findByIsActiveTrue() : repository.findByRuleTypeAndIsActiveTrue(type);
        return ResponseEntity.ok(ApiResponse.success("OK", rules));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<DiscountRule>> create(@RequestBody DiscountRule rule) {
        rule.setId(null);
        rule.setIsActive(true);
        var saved = repository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Creado", saved));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        var opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var rule = opt.get();
        rule.setIsActive(false);
        repository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Desactivado"));
    }
}
