package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.model.CommissionConfig;
import com.drakkarpress.platform.repository.CommissionConfigRepository;
import com.drakkarpress.platform.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/commission-config")
public class AdminCommissionController {

    private final CommissionConfigRepository repository;

    public AdminCommissionController(CommissionConfigRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionConfig>>> list(@RequestParam(required = false) String context) {
        List<CommissionConfig> configs = (context == null)
                ? repository.findAll()
                : repository.findByContextAndIsActiveTrueOrderByMinVolumeAsc(context);
        return ResponseEntity.ok(ApiResponse.success("OK", configs));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<CommissionConfig>> create(@RequestBody CommissionConfig config) {
        if (config.getContext() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("context requerido"));
        }
        config.setId(null);
        config.setIsActive(true);
        var saved = repository.save(config);
        return ResponseEntity.ok(ApiResponse.success("Creado", saved));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        var opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var cfg = opt.get();
        cfg.setIsActive(false);
        repository.save(cfg);
        return ResponseEntity.ok(ApiResponse.success("Desactivado"));
    }
}
