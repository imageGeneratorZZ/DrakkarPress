package com.drakkarpress.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Servicio de integración con Lulu.com para Print-On-Demand
 * Gestiona la creación de jobs de impresión, tracking y fulfillment
 */
@Slf4j
@Service
public class LuluService {

    @Value("${lulu.api.url:https://api.lulu.com}")
    private String apiUrl;

    @Value("${lulu.api.key:}")
    private String apiKey;

    @Value("${lulu.api.secret:}")
    private String apiSecret;

    @Value("${lulu.sandbox:true}")
    private boolean sandbox;

    private final RestTemplate restTemplate;
    private final ShopifyService shopifyService;

    public LuluService(ShopifyService shopifyService) {
        this.restTemplate = new RestTemplate();
        this.shopifyService = shopifyService;
    }

    /**
     * Crear headers de autenticación para Lulu API
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodeCredentials());
        headers.set("Cache-Control", "no-cache");
        return headers;
    }

    /**
     * Codificar credenciales en Base64
     */
    private String encodeCredentials() {
        String credentials = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Crear print job en Lulu
     * 
     * @param orderData Datos de la orden (bookId, título, portada URL, interior URL, cantidad, shipping info)
     * @return Print job ID de Lulu
     */
    public String createPrintJob(Map<String, Object> orderData) {
        try {
            String bookTitle = (String) orderData.get("title");
            String coverUrl = (String) orderData.get("coverUrl");
            String interiorUrl = (String) orderData.get("interiorUrl");
            int quantity = (int) orderData.getOrDefault("quantity", 1);
            Map<String, String> shippingAddress = (Map<String, String>) orderData.get("shippingAddress");

            log.info("📖 Creating Lulu print job for: {} (Qty: {})", bookTitle, quantity);

            // Construir payload para Lulu API
            Map<String, Object> printable = new HashMap<>();
            printable.put("external_id", orderData.get("orderId"));
            printable.put("name", bookTitle);
            
            // Especificaciones del libro
            Map<String, Object> spec = new HashMap<>();
            spec.put("binding_type", orderData.getOrDefault("bindingType", "PERFECT_BOUND"));
            spec.put("trim_size", orderData.getOrDefault("trimSize", "US_TRADE"));
            spec.put("page_count", orderData.getOrDefault("pageCount", 100));
            spec.put("interior_color", orderData.getOrDefault("interiorColor", "BW"));
            spec.put("paper_type", orderData.getOrDefault("paperType", "WHITE"));
            spec.put("cover_finish", orderData.getOrDefault("coverFinish", "GLOSS"));
            printable.put("specification", spec);

            // Archivos (cover + interior)
            List<Map<String, String>> files = new ArrayList<>();
            files.add(Map.of("type", "COVER", "url", coverUrl));
            files.add(Map.of("type", "INTERIOR", "url", interiorUrl));
            printable.put("files", files);

            // Line item
            List<Map<String, Object>> lineItems = new ArrayList<>();
            Map<String, Object> lineItem = new HashMap<>();
            lineItem.put("printable_normalization", printable);
            lineItem.put("quantity", quantity);
            lineItems.add(lineItem);

            // Shipping
            Map<String, Object> shipping = new HashMap<>();
            shipping.put("level", orderData.getOrDefault("shippingLevel", "MAIL"));
            
            if (shippingAddress != null) {
                Map<String, String> address = new HashMap<>();
                address.put("name", shippingAddress.get("name"));
                address.put("street1", shippingAddress.get("street1"));
                address.put("street2", shippingAddress.getOrDefault("street2", ""));
                address.put("city", shippingAddress.get("city"));
                address.put("state_code", shippingAddress.get("state"));
                address.put("postcode", shippingAddress.get("zip"));
                address.put("country_code", shippingAddress.getOrDefault("country", "US"));
                address.put("phone_number", shippingAddress.getOrDefault("phone", ""));
                address.put("email", shippingAddress.get("email"));
                shipping.put("address", address);
            }

            // Print job completo
            Map<String, Object> printJob = new HashMap<>();
            printJob.put("line_items", lineItems);
            printJob.put("shipping_address", shipping.get("address"));
            printJob.put("shipping_level", shipping.get("level"));
            printJob.put("contact_email", orderData.getOrDefault("contactEmail", "orders@drakkarpress.com"));

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(printJob, headers);

            String endpoint = sandbox ? "/v1/print-jobs/" : "/v1/print-jobs/";
            String url = apiUrl + endpoint;

            log.info("📤 Sending print job to Lulu: {}", url);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                url,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String jobId = String.valueOf(response.getBody().get("id"));
                log.info("✅ Lulu print job created: {}", jobId);
                return jobId;
            }

            throw new RuntimeException("Failed to create Lulu print job");

        } catch (Exception e) {
            log.error("❌ Error creating Lulu print job: {}", e.getMessage(), e);
            throw new RuntimeException("Lulu print job creation failed", e);
        }
    }

    /**
     * Obtener estado de un print job
     * 
     * @param jobId ID del print job en Lulu
     * @return Estado del job (CREATED, REJECTED, IN_PRODUCTION, SHIPPED, etc.)
     */
    public Map<String, Object> getJobStatus(String jobId) {
        try {
            String url = apiUrl + "/v1/print-jobs/" + jobId + "/";

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
                Map<String, Object> job = response.getBody();
                String status = (String) job.get("status");
                log.info("📊 Job {} status: {}", jobId, status);
                return job;
            }

            throw new RuntimeException("Failed to get job status");

        } catch (Exception e) {
            log.error("❌ Error getting job status: {}", e.getMessage());
            throw new RuntimeException("Failed to get Lulu job status", e);
        }
    }

    /**
     * Obtener tracking de envío
     * 
     * @param jobId ID del print job en Lulu
     * @return Información de tracking (carrier, tracking number, URL)
     */
    public Map<String, String> getShippingTracking(String jobId) {
        try {
            Map<String, Object> job = getJobStatus(jobId);
            String status = (String) job.get("status");

            if ("SHIPPED".equals(status) && job.containsKey("shipping_information")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> shippingInfo = (Map<String, Object>) job.get("shipping_information");
                
                String trackingNumber = (String) shippingInfo.get("tracking_id");
                String carrier = (String) shippingInfo.get("carrier");
                String trackingUrl = (String) shippingInfo.get("tracking_url");

                Map<String, String> tracking = new HashMap<>();
                tracking.put("trackingNumber", trackingNumber);
                tracking.put("carrier", carrier);
                tracking.put("trackingUrl", trackingUrl);
                tracking.put("status", "SHIPPED");

                log.info("📦 Tracking info for job {}: {} - {}", jobId, carrier, trackingNumber);
                return tracking;
            }

            // Job no enviado aún
            Map<String, String> tracking = new HashMap<>();
            tracking.put("status", status);
            return tracking;

        } catch (Exception e) {
            log.error("❌ Error getting tracking: {}", e.getMessage());
            return Map.of("status", "UNKNOWN", "error", e.getMessage());
        }
    }

    /**
     * Sincronizar tracking con Shopify cuando el libro sea enviado
     * 
     * @param luluJobId ID del print job en Lulu
     * @param shopifyOrderId ID de la orden en Shopify
     */
    public void syncTrackingToShopify(String luluJobId, String shopifyOrderId) {
        try {
            log.info("🔄 Syncing tracking from Lulu ({}) to Shopify ({})", luluJobId, shopifyOrderId);

            Map<String, String> tracking = getShippingTracking(luluJobId);
            String status = tracking.get("status");

            if ("SHIPPED".equals(status)) {
                String trackingNumber = tracking.get("trackingNumber");
                String trackingUrl = tracking.get("trackingUrl");

                // Actualizar fulfillment en Shopify
                boolean updated = shopifyService.updateOrderStatus(shopifyOrderId, trackingNumber, trackingUrl);

                if (updated) {
                    log.info("✅ Tracking synced to Shopify successfully");
                } else {
                    log.warn("⚠️ Failed to update Shopify fulfillment");
                }
            } else {
                log.info("📋 Job not shipped yet, status: {}", status);
            }

        } catch (Exception e) {
            log.error("❌ Error syncing tracking: {}", e.getMessage());
        }
    }

    /**
     * Calcular costo de impresión (pricing)
     * 
     * @param spec Especificaciones del libro
     * @return Costo estimado en USD
     */
    public Map<String, Object> calculatePrintingCost(Map<String, Object> spec) {
        try {
            log.info("💰 Calculating printing cost");

            // Endpoint de pricing
            String url = apiUrl + "/v1/print-jobs/cost/";

            Map<String, Object> costRequest = new HashMap<>();
            costRequest.put("line_items", List.of(
                Map.of(
                    "quantity", spec.getOrDefault("quantity", 1),
                    "printable_normalization", Map.of(
                        "specification", Map.of(
                            "binding_type", spec.getOrDefault("bindingType", "PERFECT_BOUND"),
                            "trim_size", spec.getOrDefault("trimSize", "US_TRADE"),
                            "page_count", spec.getOrDefault("pageCount", 100),
                            "interior_color", spec.getOrDefault("interiorColor", "BW"),
                            "paper_type", spec.getOrDefault("paperType", "WHITE"),
                            "cover_finish", spec.getOrDefault("coverFinish", "GLOSS")
                        )
                    )
                )
            ));
            costRequest.put("shipping_level", spec.getOrDefault("shippingLevel", "MAIL"));
            costRequest.put("shipping_address", Map.of("country_code", spec.getOrDefault("country", "US")));

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(costRequest, headers);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                url,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> cost = response.getBody();
                log.info("💵 Cost calculated: {}", cost);
                return cost;
            }

            throw new RuntimeException("Failed to calculate cost");

        } catch (Exception e) {
            log.error("❌ Error calculating cost: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * Health check para validar conexión con Lulu
     */
    public boolean isHealthy() {
        try {
            // Simple ping al endpoint de account
            String url = apiUrl + "/";
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
            );

            boolean healthy = response.getStatusCode().is2xxSuccessful();
            log.info(healthy ? "✅ Lulu API is healthy" : "⚠️ Lulu API unreachable");
            return healthy;

        } catch (Exception e) {
            log.error("❌ Lulu health check failed: {}", e.getMessage());
            return false;
        }
    }
}
