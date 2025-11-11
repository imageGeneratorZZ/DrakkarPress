package com.drakkarpress.backend.controller;

import com.drakkarpress.backend.dto.lulu.LuluBookSpecificationDTO;
import com.drakkarpress.backend.dto.lulu.LuluPrintJobDTO;
import com.drakkarpress.backend.service.LuluPrintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/lulu")
@RequiredArgsConstructor
public class LuluController {
    
    private final LuluPrintService luluPrintService;
    
    /**
     * Calcular costo de impresión para un libro
     * GET /api/lulu/pricing
     */
    @GetMapping("/pricing")
    public ResponseEntity<Map<String, Object>> calculatePricing(
            @RequestParam int pageCount,
            @RequestParam(defaultValue = "PERFECT_BOUND") String bindingType,
            @RequestParam(defaultValue = "GLOSS") String coverFinish,
            @RequestParam(defaultValue = "US_TRADE_6X9") String trimSize,
            @RequestParam(defaultValue = "WHITE") String paperType
    ) {
        try {
            LuluBookSpecificationDTO spec = LuluBookSpecificationDTO.builder()
                .pageCount(pageCount)
                .bindingType(bindingType)
                .coverFinish(coverFinish)
                .trimSize(trimSize)
                .paperType(paperType)
                .build();
            
            BigDecimal printCost = luluPrintService.calculatePrintCost(spec);
            
            // Calcular precios sugeridos
            BigDecimal authorRevenue = printCost.multiply(new BigDecimal("0.40")); // 40% ganancia autor
            BigDecimal retailPrice = printCost.add(authorRevenue);
            
            Map<String, Object> pricing = new HashMap<>();
            pricing.put("printCost", printCost);
            pricing.put("authorRevenue", authorRevenue);
            pricing.put("retailPrice", retailPrice);
            pricing.put("pageCount", pageCount);
            pricing.put("bindingType", bindingType);
            pricing.put("coverFinish", coverFinish);
            
            return ResponseEntity.ok(pricing);
            
        } catch (Exception e) {
            log.error("❌ Error calculating pricing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to calculate pricing"));
        }
    }
    
    /**
     * Crear libro en Lulu.com
     * POST /api/lulu/books
     */
    @PostMapping("/books")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createBook(
            @RequestBody LuluBookSpecificationDTO bookSpec
    ) {
        try {
            log.info("📚 Creating book in Lulu: {}", bookSpec.getTitle());
            
            // Calcular costo de impresión
            BigDecimal printCost = luluPrintService.calculatePrintCost(bookSpec);
            bookSpec.setPrintCost(printCost);
            
            // Crear libro en Lulu
            String luluBookId = luluPrintService.createBook(bookSpec);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("luluBookId", luluBookId);
            response.put("title", bookSpec.getTitle());
            response.put("author", bookSpec.getAuthor());
            response.put("printCost", printCost);
            response.put("message", "Book created successfully in Lulu.com");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create book: " + e.getMessage()));
        }
    }
    
    /**
     * Crear orden de impresión
     * POST /api/lulu/print-jobs
     */
    @PostMapping("/print-jobs")
    @PreAuthorize("hasRole('USER') or hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createPrintJob(
            @RequestParam String luluBookId,
            @RequestParam int quantity,
            @RequestBody LuluPrintJobDTO.ShippingAddressDTO shippingAddress
    ) {
        try {
            log.info("🖨️ Creating print job for book: {} (qty: {})", luluBookId, quantity);
            
            LuluPrintJobDTO printJob = luluPrintService.createPrintJob(
                luluBookId, 
                shippingAddress, 
                quantity
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", printJob.getOrderId());
            response.put("status", printJob.getStatus());
            response.put("quantity", quantity);
            response.put("message", "Print job created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating print job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create print job: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar estado de orden
     * GET /api/lulu/print-jobs/{orderId}
     */
    @GetMapping("/print-jobs/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPrintJobStatus(
            @PathVariable String orderId
    ) {
        try {
            LuluPrintJobDTO printJob = luluPrintService.getPrintJobStatus(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", printJob.getOrderId());
            response.put("status", printJob.getStatus());
            response.put("trackingNumber", printJob.getTrackingNumber());
            response.put("trackingUrl", printJob.getTrackingUrl());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting print job status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get print job status: " + e.getMessage()));
        }
    }
    
    /**
     * Cancelar orden de impresión
     * DELETE /api/lulu/print-jobs/{orderId}
     */
    @DeleteMapping("/print-jobs/{orderId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cancelPrintJob(
            @PathVariable String orderId
    ) {
        try {
            boolean cancelled = luluPrintService.cancelPrintJob(orderId);
            
            if (cancelled) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Print job cancelled successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Print job cannot be cancelled (may already be shipped)"));
            }
            
        } catch (Exception e) {
            log.error("❌ Error cancelling print job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to cancel print job: " + e.getMessage()));
        }
    }
}
