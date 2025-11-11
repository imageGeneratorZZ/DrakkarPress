package com.drakkarpress.backend.controller;

import com.drakkarpress.backend.service.ShopifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/shopify")
@RequiredArgsConstructor
public class ShopifyController {
    
    private final ShopifyService shopifyService;
    
    /**
     * Crear o actualizar producto en Shopify
     * POST /api/shopify/products
     */
    @PostMapping("/products")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestBody Map<String, Object> productData
    ) {
        try {
            String title = (String) productData.get("title");
            log.info("🛒 Creating Shopify product: {}", title);
            
            String productId = shopifyService.createOrUpdateProduct(productData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productId", productId);
            response.put("title", title);
            response.put("message", "Product created successfully in Shopify");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating Shopify product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create product: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar inventario
     * PUT /api/shopify/products/{productId}/inventory
     */
    @PutMapping("/products/{productId}/inventory")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateInventory(
            @PathVariable String productId,
            @RequestParam int quantity
    ) {
        try {
            boolean updated = shopifyService.updateInventory(productId, quantity);
            
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Inventory updated successfully",
                    "quantity", quantity
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update inventory"));
            }
            
        } catch (Exception e) {
            log.error("❌ Error updating inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update inventory: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar producto
     * DELETE /api/shopify/products/{productId}
     */
    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @PathVariable String productId
    ) {
        try {
            boolean deleted = shopifyService.deleteProduct(productId);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product deleted successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to delete product"));
            }
            
        } catch (Exception e) {
            log.error("❌ Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete product: " + e.getMessage()));
        }
    }
    
    /**
     * Webhook de orden creada (order/create)
     * POST /api/shopify/webhooks/orders
     */
    @PostMapping("/webhooks/orders")
    public ResponseEntity<Void> handleOrderWebhook(
            @RequestBody Map<String, Object> orderData,
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmacHeader,
            @RequestBody String rawBody
    ) {
        try {
            // Verificar firma HMAC
            boolean valid = shopifyService.verifyWebhook(rawBody, hmacHeader);
            
            if (!valid) {
                log.warn("⚠️ Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Procesar orden
            shopifyService.processOrderWebhook(orderData);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("❌ Error handling order webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualizar estado de orden (tracking)
     * POST /api/shopify/orders/{orderId}/fulfillment
     */
    @PostMapping("/orders/{orderId}/fulfillment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String trackingNumber,
            @RequestParam String trackingUrl
    ) {
        try {
            boolean updated = shopifyService.updateOrderStatus(orderId, trackingNumber, trackingUrl);
            
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order status updated successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update order status"));
            }
            
        } catch (Exception e) {
            log.error("❌ Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update order status: " + e.getMessage()));
        }
    }
}
