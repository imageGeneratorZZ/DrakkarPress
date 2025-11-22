package com.drakkarpress.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Conector para Lulu (Print-on-Demand)
 * Distribución automática de libros impresos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LuluConnectorService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.lulu.api-url:https://api.lulu.com/v1}")
    private String luluApiUrl;

    @Value("${app.lulu.api-key:}")
    private String apiKey;

    @Value("${app.lulu.publisher-id:}")
    private String publisherId;

    /**
     * Crea proyecto de impresión en Lulu
     * Soporta formatos: Paperback, Hardcover, Coil-Bound
     */
    public Map<String, Object> createPrintProject(String title, String author, String description,
                                                    File printPdfFile, File coverFile, 
                                                    String isbn, Map<String, Object> printSpecs) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Lulu API not configured - generating manual export package");
            return generateManualExportPackage(title, author, description, printPdfFile, coverFile, isbn, printSpecs);
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(luluApiUrl).build();

            // Payload para Lulu Print API
            Map<String, Object> request = new HashMap<>();
            request.put("title", title);
            request.put("author", author);
            request.put("description", description);
            request.put("isbn", isbn);
            request.put("publisher_id", publisherId);
            
            // Especificaciones de impresión
            request.put("trim_size", printSpecs.getOrDefault("trim_size", "6x9")); // inches
            request.put("binding", printSpecs.getOrDefault("binding", "PAPERBACK_STANDARD"));
            request.put("interior_color", printSpecs.getOrDefault("interior_color", "BW")); // BW o COLOR
            request.put("paper_type", printSpecs.getOrDefault("paper_type", "WHITE"));

            // TODO: Multipart upload de PDF interior y cover
            // Por ahora retornamos manual export
            return generateManualExportPackage(title, author, description, printPdfFile, coverFile, isbn, printSpecs);

        } catch (Exception e) {
            log.error("Exception in Lulu project creation", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    /**
     * Genera paquete de exportación manual para Lulu
     */
    private Map<String, Object> generateManualExportPackage(String title, String author, 
                                                              String description, File printPdfFile, 
                                                              File coverFile, String isbn,
                                                              Map<String, Object> printSpecs) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "MANUAL_EXPORT_REQUIRED");
        result.put("platform", "Lulu (Print-on-Demand)");
        result.put("instructions", "Please visit Lulu.com to create print project manually");
        result.put("files", Map.of(
            "interior_pdf", printPdfFile.getAbsolutePath(),
            "cover", coverFile.getAbsolutePath()
        ));
        result.put("metadata", Map.of(
            "title", title,
            "author", author,
            "description", description,
            "isbn", isbn != null ? isbn : "To be assigned by Lulu"
        ));
        result.put("print_specs", printSpecs);
        result.put("lulu_url", "https://www.lulu.com/create");
        result.put("help_url", "https://www.lulu.com/sell/sell-on-lulu");
        
        log.info("Generated Lulu manual export package for: {}", title);
        return result;
    }

    /**
     * Obtiene estado de proyecto de impresión en Lulu
     */
    public Map<String, Object> getProjectStatus(String luluExternalId) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Map.of("status", "UNKNOWN", "message", "Lulu API not configured");
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(luluApiUrl).build();

            Map<String, Object> response = webClient.get()
                    .uri("/projects/" + luluExternalId)
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response != null ? response : Map.of("status", "UNKNOWN");

        } catch (Exception e) {
            log.error("Error checking Lulu project status", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    /**
     * Calcula costo de impresión basado en especificaciones
     * Lulu cobra: costo de impresión + comisión por venta
     */
    public Map<String, Double> calculatePrintCost(int pageCount, String trimSize, 
                                                   String binding, String interiorColor) {
        // Fórmula aproximada de Lulu (actualizar con API real)
        double baseCost = 0.0;
        
        // Costo por página (BW vs COLOR)
        double perPageCost = "COLOR".equals(interiorColor) ? 0.12 : 0.0175;
        double pageCost = pageCount * perPageCost;
        
        // Costo de cover
        double coverCost = "HARDCOVER".equals(binding) ? 3.50 : 1.00;
        
        // Fixed fee
        double fixedFee = 0.75;
        
        baseCost = pageCost + coverCost + fixedFee;
        
        Map<String, Double> costs = new HashMap<>();
        costs.put("print_cost", baseCost);
        costs.put("recommended_retail_price", baseCost * 2.5); // Margen sugerido
        costs.put("author_royalty_at_recommended", baseCost * 1.5); // Ganancia autor
        
        return costs;
    }

    private Map<String, Object> generateErrorResponse(String message) {
        return Map.of("status", "ERROR", "message", message);
    }
}
