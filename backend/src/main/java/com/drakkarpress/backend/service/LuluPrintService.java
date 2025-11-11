package com.drakkarpress.backend.service;

import com.drakkarpress.backend.dto.lulu.LuluBookSpecificationDTO;
import com.drakkarpress.backend.dto.lulu.LuluPrintJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LuluPrintService {
    
    @Value("${lulu.client.key}")
    private String clientKey;
    
    @Value("${lulu.client.secret}")
    private String clientSecret;
    
    @Value("${lulu.api.url}")
    private String apiUrl;
    
    @Value("${lulu.api.base64}")
    private String apiBase64;
    
    private final RestTemplate restTemplate;
    private String accessToken;
    
    public LuluPrintService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Obtener token de acceso OAuth2 de Lulu.com
     */
    private String getAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", apiBase64);
            
            String body = "grant_type=client_credentials";
            
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                "https://api.lulu.com/auth/realms/glasstree/protocol/openid-connect/token",
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                accessToken = (String) response.getBody().get("access_token");
                log.info("✅ Lulu.com access token obtained successfully");
                return accessToken;
            }
            
            throw new RuntimeException("Failed to obtain Lulu access token");
            
        } catch (Exception e) {
            log.error("❌ Error obtaining Lulu access token: {}", e.getMessage());
            throw new RuntimeException("Lulu authentication failed", e);
        }
    }
    
    /**
     * Calcular costo de impresión según especificaciones
     */
    public BigDecimal calculatePrintCost(LuluBookSpecificationDTO spec) {
        // Precios base por página (aproximados)
        BigDecimal baseCostPerPage = new BigDecimal("0.015"); // $0.015 por página
        BigDecimal coverCost = new BigDecimal("2.00"); // Costo base de portada
        
        // Multiplicadores según opciones
        BigDecimal bindingMultiplier = spec.getBindingType().equals("PERFECT_BOUND") 
            ? new BigDecimal("1.0") 
            : new BigDecimal("1.2");
        
        BigDecimal finishMultiplier = spec.getCoverFinish().equals("GLOSS")
            ? new BigDecimal("1.1")
            : new BigDecimal("1.0");
        
        // Cálculo
        BigDecimal pageCost = baseCostPerPage
            .multiply(new BigDecimal(spec.getPageCount()))
            .multiply(bindingMultiplier);
        
        BigDecimal totalCost = pageCost
            .add(coverCost)
            .multiply(finishMultiplier);
        
        log.info("📊 Print cost calculated: ${} for {} pages", totalCost, spec.getPageCount());
        return totalCost.setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Crear libro en Lulu.com
     */
    public String createBook(LuluBookSpecificationDTO spec) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", spec.getTitle());
            requestBody.put("author", spec.getAuthor());
            requestBody.put("description", spec.getDescription());
            requestBody.put("language", spec.getLanguage());
            
            // Pod Package ID según especificaciones
            // US Trade (6x9) Perfect Bound White: 0850X1100BWSTDPB060UW444MNG
            requestBody.put("pod_package_id", getPodPackageId(spec));
            
            // Archivos
            Map<String, String> files = new HashMap<>();
            files.put("interior_pdf_url", spec.getPdfUrl());
            files.put("cover_image_url", spec.getCoverUrl());
            requestBody.put("print_files", files);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                apiUrl + "/print-jobs",
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String bookId = (String) response.getBody().get("id");
                log.info("✅ Book created in Lulu.com: {}", bookId);
                return bookId;
            }
            
            throw new RuntimeException("Failed to create book in Lulu");
            
        } catch (Exception e) {
            log.error("❌ Error creating book in Lulu: {}", e.getMessage());
            throw new RuntimeException("Lulu book creation failed", e);
        }
    }
    
    /**
     * Crear orden de impresión
     */
    public LuluPrintJobDTO createPrintJob(String luluBookId, LuluPrintJobDTO.ShippingAddressDTO address, int quantity) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("line_items", new Object[]{
                Map.of(
                    "printable_normalization", luluBookId,
                    "quantity", quantity
                )
            });
            
            // Dirección de envío
            Map<String, String> shippingAddress = new HashMap<>();
            shippingAddress.put("name", address.getName());
            shippingAddress.put("street1", address.getStreet1());
            if (address.getStreet2() != null) {
                shippingAddress.put("street2", address.getStreet2());
            }
            shippingAddress.put("city", address.getCity());
            shippingAddress.put("state_code", address.getStateCode());
            shippingAddress.put("postcode", address.getPostalCode());
            shippingAddress.put("country_code", address.getCountryCode());
            if (address.getPhoneNumber() != null) {
                shippingAddress.put("phone_number", address.getPhoneNumber());
            }
            requestBody.put("shipping_address", shippingAddress);
            
            // Nivel de envío (MAIL: más barato, PRIORITY: más rápido)
            requestBody.put("shipping_level", "MAIL");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                apiUrl + "/print-jobs",
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String orderId = (String) response.getBody().get("id");
                String status = (String) response.getBody().get("status");
                
                log.info("✅ Print job created: {} (status: {})", orderId, status);
                
                return LuluPrintJobDTO.builder()
                    .orderId(orderId)
                    .status(status)
                    .quantity(quantity)
                    .shippingAddress(address)
                    .build();
            }
            
            throw new RuntimeException("Failed to create print job");
            
        } catch (Exception e) {
            log.error("❌ Error creating print job: {}", e.getMessage());
            throw new RuntimeException("Lulu print job creation failed", e);
        }
    }
    
    /**
     * Consultar estado de orden
     */
    public LuluPrintJobDTO getPrintJobStatus(String orderId) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl + "/print-jobs/" + orderId,
                HttpMethod.GET,
                request,
                (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                String status = (String) body.get("status");
                String trackingNumber = (String) body.get("tracking_number");
                String trackingUrl = (String) body.get("tracking_url");
                
                log.info("📦 Print job status: {} (tracking: {})", status, trackingNumber);
                
                return LuluPrintJobDTO.builder()
                    .orderId(orderId)
                    .status(status)
                    .trackingNumber(trackingNumber)
                    .trackingUrl(trackingUrl)
                    .build();
            }
            
            throw new RuntimeException("Failed to get print job status");
            
        } catch (Exception e) {
            log.error("❌ Error getting print job status: {}", e.getMessage());
            throw new RuntimeException("Lulu status check failed", e);
        }
    }
    
    /**
     * Obtener Pod Package ID según especificaciones
     * Referencia: https://developers.lulu.com/pod-packages
     */
    private String getPodPackageId(LuluBookSpecificationDTO spec) {
        // US Trade 6x9, Perfect Bound, White, 60-828 pages
        if (spec.getTrimSize().equals("US_TRADE_6X9") 
            && spec.getBindingType().equals("PERFECT_BOUND")
            && spec.getPaperType().equals("WHITE")
            && spec.getCoverFinish().equals("GLOSS")) {
            return "0600X0900BWSTDPB060UW444GXX";
        }
        
        // US Trade 6x9, Perfect Bound, White, Matte
        if (spec.getTrimSize().equals("US_TRADE_6X9") 
            && spec.getBindingType().equals("PERFECT_BOUND")
            && spec.getPaperType().equals("WHITE")
            && spec.getCoverFinish().equals("MATTE")) {
            return "0600X0900BWSTDPB060UW444MXX";
        }
        
        // US Letter 8.5x11, Perfect Bound, White, Gloss
        if (spec.getTrimSize().equals("US_LETTER_8.5X11")
            && spec.getBindingType().equals("PERFECT_BOUND")
            && spec.getPaperType().equals("WHITE")
            && spec.getCoverFinish().equals("GLOSS")) {
            return "0850X1100BWSTDPB060UW444GXX";
        }
        
        // Default: US Trade 6x9 Perfect Bound White Gloss
        log.warn("⚠️ No exact Pod Package ID found, using default");
        return "0600X0900BWSTDPB060UW444GXX";
    }
    
    /**
     * Cancelar orden de impresión (solo si no ha sido enviada)
     */
    public boolean cancelPrintJob(String orderId) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                apiUrl + "/print-jobs/" + orderId,
                HttpMethod.DELETE,
                request,
                Void.class
            );
            
            boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT;
            log.info(success ? "✅ Print job cancelled: {}" : "❌ Failed to cancel print job: {}", orderId);
            return success;
            
        } catch (Exception e) {
            log.error("❌ Error cancelling print job: {}", e.getMessage());
            return false;
        }
    }
}
