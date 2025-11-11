package com.drakkarpress.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class ShopifyService {
    
    @Value("${shopify.store.url}")
    private String storeUrl;
    
    @Value("${shopify.api.key}")
    private String apiKey;
    
    @Value("${shopify.api.secret}")
    private String apiSecret;
    
    @Value("${shopify.access.token}")
    private String accessToken;
    
    private final RestTemplate restTemplate;
    
    public ShopifyService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Crear headers de autenticación para Shopify API
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", accessToken);
        return headers;
    }
    
    /**
     * Crear o actualizar producto en Shopify
     */
    public String createOrUpdateProduct(Map<String, Object> productData) {
        try {
            String title = (String) productData.get("title");
            String description = (String) productData.get("description");
            String vendor = (String) productData.get("vendor");
            String productType = (String) productData.get("productType");
            List<String> tags = (List<String>) productData.get("tags");
            Map<String, Object> variant = (Map<String, Object>) productData.get("variant");
            List<String> imageUrls = (List<String>) productData.get("images");
            
            Map<String, Object> product = new HashMap<>();
            product.put("title", title);
            product.put("body_html", description);
            product.put("vendor", vendor != null ? vendor : "DrakkarPress");
            product.put("product_type", productType != null ? productType : "Book");
            product.put("tags", tags != null ? String.join(", ", tags) : "");
            
            // Variante (precio, SKU, inventario)
            List<Map<String, Object>> variants = new ArrayList<>();
            Map<String, Object> variantData = new HashMap<>();
            variantData.put("price", variant.get("price"));
            variantData.put("sku", variant.get("sku"));
            variantData.put("inventory_quantity", variant.getOrDefault("quantity", 999));
            variantData.put("inventory_management", "shopify");
            variantData.put("inventory_policy", "continue"); // Permite vender sin stock (POD)
            variants.add(variantData);
            product.put("variants", variants);
            
            // Imágenes
            if (imageUrls != null && !imageUrls.isEmpty()) {
                List<Map<String, String>> images = new ArrayList<>();
                for (String url : imageUrls) {
                    images.add(Map.of("src", url));
                }
                product.put("images", images);
            }
            
            Map<String, Object> requestBody = Map.of("product", product);
            
            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = storeUrl + "/admin/api/2024-01/products.json";
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                url,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> createdProduct = (Map<String, Object>) response.getBody().get("product");
                String productId = String.valueOf(((Number) createdProduct.get("id")).longValue());
                
                log.info("✅ Product created in Shopify: {} (ID: {})", title, productId);
                return productId;
            }
            
            throw new RuntimeException("Failed to create product in Shopify");
            
        } catch (Exception e) {
            log.error("❌ Error creating Shopify product: {}", e.getMessage());
            throw new RuntimeException("Shopify product creation failed", e);
        }
    }
    
    /**
     * Actualizar inventario de producto
     */
    public boolean updateInventory(String productId, int quantity) {
        try {
            // 1. Obtener inventory_item_id
            String url = storeUrl + "/admin/api/2024-01/products/" + productId + ".json";
            
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return false;
            }
            
            Map<String, Object> product = (Map<String, Object>) response.getBody().get("product");
            List<Map<String, Object>> variants = (List<Map<String, Object>>) product.get("variants");
            
            if (variants.isEmpty()) {
                return false;
            }
            
            Number inventoryItemId = (Number) variants.get(0).get("inventory_item_id");
            Number locationId = getLocationId();
            
            // 2. Actualizar inventario
            Map<String, Object> inventoryLevel = new HashMap<>();
            inventoryLevel.put("location_id", locationId);
            inventoryLevel.put("inventory_item_id", inventoryItemId);
            inventoryLevel.put("available", quantity);
            
            String inventoryUrl = storeUrl + "/admin/api/2024-01/inventory_levels/set.json";
            HttpEntity<Map<String, Object>> inventoryRequest = new HttpEntity<>(inventoryLevel, headers);
            
            ResponseEntity<Map<String, Object>> inventoryResponse = restTemplate.postForEntity(
                inventoryUrl,
                inventoryRequest,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            boolean success = inventoryResponse.getStatusCode() == HttpStatus.OK;
            log.info(success ? "✅ Inventory updated: {} units" : "❌ Failed to update inventory", quantity);
            return success;
            
        } catch (Exception e) {
            log.error("❌ Error updating inventory: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener ID de ubicación principal (location)
     */
    private Number getLocationId() {
        try {
            String url = storeUrl + "/admin/api/2024-01/locations.json";
            
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> locations = (List<Map<String, Object>>) response.getBody().get("locations");
                if (!locations.isEmpty()) {
                    return (Number) locations.get(0).get("id");
                }
            }
            
            throw new RuntimeException("No locations found");
            
        } catch (Exception e) {
            log.error("❌ Error getting location ID: {}", e.getMessage());
            throw new RuntimeException("Failed to get Shopify location", e);
        }
    }
    
    /**
     * Eliminar producto de Shopify
     */
    public boolean deleteProduct(String productId) {
        try {
            String url = storeUrl + "/admin/api/2024-01/products/" + productId + ".json";
            
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                Void.class
            );
            
            boolean success = response.getStatusCode() == HttpStatus.OK;
            log.info(success ? "✅ Product deleted: {}" : "❌ Failed to delete product: {}", productId);
            return success;
            
        } catch (Exception e) {
            log.error("❌ Error deleting product: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Procesar webhook de orden (order/create)
     */
    public void processOrderWebhook(Map<String, Object> orderData) {
        try {
            String orderId = String.valueOf(orderData.get("id"));
            String orderNumber = String.valueOf(orderData.get("order_number"));
            List<Map<String, Object>> lineItems = (List<Map<String, Object>>) orderData.get("line_items");
            Map<String, Object> customer = (Map<String, Object>) orderData.get("customer");
            
            log.info("📦 Processing Shopify order: #{} (ID: {})", orderNumber, orderId);
            
            // Aquí se procesaría la orden:
            // 1. Crear orden en base de datos local
            // 2. Generar libro con IA si es necesario
            // 3. Enviar a Lulu.com para impresión
            // 4. Actualizar estado en Shopify
            
            for (Map<String, Object> item : lineItems) {
                String sku = (String) item.get("sku");
                int quantity = ((Number) item.get("quantity")).intValue();
                String title = (String) item.get("title");
                
                log.info("  📚 Item: {} (SKU: {}, Qty: {})", title, sku, quantity);
            }
            
            log.info("✅ Order webhook processed successfully");
            
        } catch (Exception e) {
            log.error("❌ Error processing order webhook: {}", e.getMessage());
        }
    }
    
    /**
     * Verificar webhook signature (seguridad)
     */
    public boolean verifyWebhook(String body, String hmacHeader) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                apiSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                "HmacSHA256"
            );
            mac.init(secretKey);
            
            byte[] hash = mac.doFinal(body.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String computedHmac = Base64.getEncoder().encodeToString(hash);
            
            boolean valid = computedHmac.equals(hmacHeader);
            log.info(valid ? "✅ Webhook signature verified" : "❌ Invalid webhook signature");
            return valid;
            
        } catch (Exception e) {
            log.error("❌ Error verifying webhook: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualizar estado de orden (fulfillment)
     */
    public boolean updateOrderStatus(String orderId, String trackingNumber, String trackingUrl) {
        try {
            // 1. Crear fulfillment
            Map<String, Object> fulfillment = new HashMap<>();
            fulfillment.put("location_id", getLocationId());
            fulfillment.put("tracking_number", trackingNumber);
            fulfillment.put("tracking_url", trackingUrl);
            fulfillment.put("notify_customer", true);
            
            Map<String, Object> requestBody = Map.of("fulfillment", fulfillment);
            
            String url = storeUrl + "/admin/api/2024-01/orders/" + orderId + "/fulfillments.json";
            
            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                url,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            boolean success = response.getStatusCode() == HttpStatus.CREATED;
            log.info(success ? "✅ Order fulfillment created: {}" : "❌ Failed to create fulfillment", orderId);
            return success;
            
        } catch (Exception e) {
            log.error("❌ Error updating order status: {}", e.getMessage());
            return false;
        }
    }
}
