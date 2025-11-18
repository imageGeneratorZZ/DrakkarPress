package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.security.JwtTokenProvider;
import com.drakkarpress.platform.service.BookPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador de Compra de Ebooks
 * 
 * Endpoints:
 * - POST /api/books/purchase/checkout - Crear checkout para ebook
 * - GET /api/books/my-library - Biblioteca del usuario
 * - POST /api/books/purchase/{id}/regenerate-link - Regenerar link de descarga
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookPurchaseController {

    private final BookPurchaseService purchaseService;
    private final JwtTokenProvider tokenProvider;

    /**
     * Crear checkout para comprar ebook
     * 
     * POST /api/books/purchase/checkout
     * Authorization: Bearer token
     * Body: { "bookId": "uuid", "format": "PDF" }
     */
    @PostMapping("/purchase/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCheckout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        try {
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);
            
            UUID bookId = UUID.fromString(request.get("bookId"));
            String format = request.getOrDefault("format", "PDF");

            log.info("Creando checkout para ebook - Usuario: {} - Libro: {} - Formato: {}", 
                     userId, bookId, format);

            Map<String, Object> checkout = purchaseService.createEbookCheckout(userId, bookId, format);

            return ResponseEntity.ok(ApiResponse.success("Checkout creado", checkout));

        } catch (Exception e) {
            log.error("Error creando checkout: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear checkout: " + e.getMessage()));
        }
    }

    /**
     * Obtener biblioteca de ebooks del usuario
     * 
     * GET /api/books/my-library
     * Authorization: Bearer token
     */
    @GetMapping("/my-library")
    public ResponseEntity<ApiResponse<List<BookPurchase>>> getMyLibrary(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);

            List<BookPurchase> library = purchaseService.getUserLibrary(userId);

            return ResponseEntity.ok(ApiResponse.success("Biblioteca obtenida", library));

        } catch (Exception e) {
            log.error("Error obteniendo biblioteca: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener biblioteca"));
        }
    }

    /**
     * Regenerar link de descarga
     * 
     * POST /api/books/purchase/{purchaseId}/regenerate-link
     * Authorization: Bearer token
     */
    @PostMapping("/purchase/{purchaseId}/regenerate-link")
    public ResponseEntity<ApiResponse<Map<String, String>>> regenerateDownloadLink(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID purchaseId) {
        
        try {
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);

            String newLink = purchaseService.regenerateDownloadLink(purchaseId, userId);

            Map<String, String> response = new HashMap<>();
            response.put("downloadLink", newLink);

            return ResponseEntity.ok(ApiResponse.success("Link regenerado", response));

        } catch (Exception e) {
            log.error("Error regenerando link: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Verificar si usuario compró un libro
     * 
     * GET /api/books/{bookId}/purchased
     * Authorization: Bearer token
     */
    @GetMapping("/{bookId}/purchased")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPurchased(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID bookId) {
        
        try {
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);

            boolean purchased = purchaseService.hasUserPurchased(userId, bookId);

            Map<String, Boolean> response = new HashMap<>();
            response.put("purchased", purchased);

            return ResponseEntity.ok(ApiResponse.success("Estado verificado", response));

        } catch (Exception e) {
            log.error("Error verificando compra: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar compra"));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Book purchase service is healthy", "OK"));
    }
}
