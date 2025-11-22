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
 * Conector para Google Play Books Partner Center
 * Distribución automática a Google Play Books Store
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GooglePlayConnectorService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.googleplay.api-url:https://www.googleapis.com/books/v1}")
    private String googlePlayApiUrl;

    @Value("${app.googleplay.api-key:}")
    private String apiKey;

    @Value("${app.googleplay.client-id:}")
    private String clientId;

    /**
     * Publica libro en Google Play Books
     * Usa Google Books Partner Center API
     */
    public Map<String, Object> publishToGooglePlay(String title, String author, String description,
                                                     File epubFile, File coverFile, String isbn) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Google Play API not configured - generating manual export package");
            return generateManualExportPackage(title, author, description, epubFile, coverFile, isbn);
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(googlePlayApiUrl).build();

            // Upload EPUB y metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", title);
            metadata.put("authors", new String[]{author});
            metadata.put("description", description);
            if (isbn != null) {
                metadata.put("industryIdentifiers", Map.of("type", "ISBN_13", "identifier", isbn));
            }

            // TODO: Multipart upload de EPUB y cover
            // Por ahora retornamos manual export
            return generateManualExportPackage(title, author, description, epubFile, coverFile, isbn);

        } catch (Exception e) {
            log.error("Exception in Google Play publish", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    /**
     * Genera paquete de exportación manual para Google Play Books
     */
    private Map<String, Object> generateManualExportPackage(String title, String author, 
                                                              String description, File epubFile, 
                                                              File coverFile, String isbn) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "MANUAL_EXPORT_REQUIRED");
        result.put("platform", "Google Play Books");
        result.put("instructions", "Please visit Google Play Books Partner Center to complete publication");
        result.put("files", Map.of(
            "manuscript", epubFile.getAbsolutePath(),
            "cover", coverFile.getAbsolutePath()
        ));
        result.put("metadata", Map.of(
            "title", title,
            "author", author,
            "description", description,
            "isbn", isbn != null ? isbn : "To be assigned"
        ));
        result.put("partner_center_url", "https://play.google.com/books/publish");
        result.put("help_url", "https://support.google.com/books/partner");
        
        log.info("Generated Google Play manual export package for: {}", title);
        return result;
    }

    /**
     * Obtiene estado de publicación en Google Play Books
     */
    public Map<String, Object> getPublicationStatus(String googlePlayExternalId) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Map.of("status", "UNKNOWN", "message", "Google Play API not configured");
        }

        try {
            WebClient webClient = webClientBuilder.baseUrl(googlePlayApiUrl).build();

            Map<String, Object> response = webClient.get()
                    .uri("/volumes/" + googlePlayExternalId + "?key=" + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response != null ? response : Map.of("status", "UNKNOWN");

        } catch (Exception e) {
            log.error("Error checking Google Play status", e);
            return generateErrorResponse(e.getMessage());
        }
    }

    private Map<String, Object> generateErrorResponse(String message) {
        return Map.of("status", "ERROR", "message", message);
    }
}
