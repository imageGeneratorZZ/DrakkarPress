package com.drakkarpress.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Conector para Amazon Kindle Direct Publishing (KDP)
 * API de distribución automática a Amazon Kindle Store
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KdpConnectorService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.kdp.api-url:}")
    private String kdpApiUrl;

    @Value("${app.kdp.api-key:}")
    private String kdpApiKey;

    @Value("${app.kdp.publisher-id:}")
    private String publisherId;

    /**
     * Publica libro en KDP
     * Nota: Amazon no tiene API pública oficial para KDP
     * Opciones:
     * 1. Usar KDP Publisher API (requiere aprobación especial de Amazon)
     * 2. Integración manual vía web scraping (frágil, no recomendado)
     * 3. Export + instrucciones manual para autor
     */
    public Map<String, Object> publishToKdp(String title, String author, String description,
                                             File epubFile, File coverFile, String isbn) {
        if (kdpApiUrl == null || kdpApiUrl.isEmpty()) {
            log.warn("KDP API not configured - generating manual export package");
            return generateManualExportPackage(title, author, description, epubFile, coverFile, isbn);
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(kdpApiUrl).build();

            // Payload para KDP Publisher API (si está disponible)
            Map<String, Object> request = new HashMap<>();
            request.put("title", title);
            request.put("author", author);
            request.put("description", description);
            request.put("isbn", isbn);
            request.put("publisher_id", publisherId);
            // TODO: Upload de archivos vía multipart

            Map<String, Object> response = webClient.post()
                    .uri("/publish")
                    .header("Authorization", "Bearer " + kdpApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(e -> {
                        log.error("Error publishing to KDP", e);
                        return Mono.just(generateErrorResponse(e.getMessage()));
                    })
                    .block();

            log.info("Published to KDP: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Exception in KDP publish", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    /**
     * Genera paquete de exportación manual para KDP
     * Devuelve instrucciones y URLs para que el autor complete manualmente
     */
    private Map<String, Object> generateManualExportPackage(String title, String author, 
                                                              String description, File epubFile, 
                                                              File coverFile, String isbn) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "MANUAL_EXPORT_REQUIRED");
        result.put("platform", "Amazon KDP");
        result.put("instructions", "Please visit https://kdp.amazon.com to complete publication manually");
        result.put("files", Map.of(
            "manuscript", epubFile.getAbsolutePath(),
            "cover", coverFile.getAbsolutePath()
        ));
        result.put("metadata", Map.of(
            "title", title,
            "author", author,
            "description", description,
            "isbn", isbn != null ? isbn : "To be assigned by KDP"
        ));
        result.put("kdp_url", "https://kdp.amazon.com/en_US/bookshelf");
        result.put("help_url", "https://kdp.amazon.com/en_US/help");
        
        log.info("Generated KDP manual export package for: {}", title);
        return result;
    }

    /**
     * Obtiene estado de publicación en KDP
     */
    public Map<String, Object> getPublicationStatus(String kdpExternalId) {
        if (kdpApiUrl == null || kdpApiUrl.isEmpty()) {
            log.warn("KDP API not configured");
            return Map.of("status", "UNKNOWN", "message", "KDP API not configured");
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(kdpApiUrl).build();

            Map<String, Object> response = webClient.get()
                    .uri("/publication/" + kdpExternalId)
                    .header("Authorization", "Bearer " + kdpApiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response != null ? response : Map.of("status", "UNKNOWN");

        } catch (Exception e) {
            log.error("Error checking KDP status", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    /**
     * Actualiza precio de libro en KDP
     */
    public boolean updatePricing(String kdpExternalId, Map<String, Double> territoryPricing) {
        if (kdpApiUrl == null || kdpApiUrl.isEmpty()) {
            log.warn("KDP API not configured - pricing must be updated manually");
            return false;
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(kdpApiUrl).build();

            Map<String, Object> request = new HashMap<>();
            request.put("kdp_id", kdpExternalId);
            request.put("pricing", territoryPricing);

            webClient.put()
                    .uri("/publication/" + kdpExternalId + "/pricing")
                    .header("Authorization", "Bearer " + kdpApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Updated KDP pricing for: {}", kdpExternalId);
            return true;

        } catch (Exception e) {
            log.error("Error updating KDP pricing", e);
            return false;
        }
    }

    private Map<String, Object> generateErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERROR");
        error.put("message", message);
        return error;
    }
}
