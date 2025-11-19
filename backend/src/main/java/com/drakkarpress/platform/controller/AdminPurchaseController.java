package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.repository.BookPurchaseRepository;
import com.drakkarpress.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador Admin para gestión de compras
 * Solo accesible por ADMIN o AUTHOR
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPurchaseController {

    private final BookPurchaseRepository purchaseRepository;
    private final JwtTokenProvider tokenProvider;

    /**
     * GET /api/admin/purchases
     * Lista TODAS las compras (admin view) con dedicatorias incluidas
     * Requiere rol ADMIN o AUTHOR
     */
    @GetMapping("/purchases")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AUTHOR')")
    public ResponseEntity<ApiResponse<List<BookPurchase>>> getAllPurchases(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7);
            tokenProvider.validateToken(token);

            // Obtener todas las compras ordenadas por fecha (más recientes primero)
            List<BookPurchase> purchases = purchaseRepository.findAll();
            purchases.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            log.info("Admin consultó {} compras (con dedicatorias)", purchases.size());

            return ResponseEntity.ok(ApiResponse.success("Compras obtenidas", purchases));

        } catch (Exception e) {
            log.error("Error obteniendo compras admin: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener compras"));
        }
    }

    /**
     * GET /api/admin/purchases/with-dedication
     * Lista solo compras con dedicatoria
     */
    @GetMapping("/purchases/with-dedication")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AUTHOR')")
    public ResponseEntity<ApiResponse<List<BookPurchase>>> getPurchasesWithDedication(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7);
            tokenProvider.validateToken(token);

            List<BookPurchase> purchases = purchaseRepository.findAll();
            List<BookPurchase> withDedication = purchases.stream()
                    .filter(p -> p.getDedicationMessage() != null && !p.getDedicationMessage().isBlank())
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .toList();

            log.info("Admin consultó {} compras con dedicatoria", withDedication.size());

            return ResponseEntity.ok(ApiResponse.success("Compras con dedicatoria", withDedication));

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener compras"));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Admin purchase service is healthy", "OK"));
    }
}
