package com.drakkarpress.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiGeneratorService {

    private final WebClient webClient;
    
    @Value("${api.investigatron.url:http://localhost:8000}")
    private String investigatronUrl;
    
    @Value("${api.investigatron.api-key:}")
    private String investigatronApiKey;

    public AiGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Genera una idea de libro basada en género y palabras clave
     */
    public Mono<String> generateBookIdea(String genre, String keywords) {
        Map<String, Object> request = new HashMap<>();
        request.put("genre", genre);
        request.put("keywords", keywords);
        request.put("type", "book_idea");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/idea")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error generando idea: " + e.getMessage()));
    }

    /**
     * Extiende un capítulo existente
     */
    public Mono<String> extendChapter(String currentText, String direction, int words) {
        Map<String, Object> request = new HashMap<>();
        request.put("current_text", currentText);
        request.put("direction", direction);
        request.put("target_words", words);
        request.put("type", "chapter_extension");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/chapter")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error extendiendo capítulo: " + e.getMessage()));
    }

    /**
     * Genera una sinopsis basada en el contenido del libro
     */
    public Mono<String> generateSynopsis(String bookContent, int maxLength) {
        Map<String, Object> request = new HashMap<>();
        request.put("content", bookContent);
        request.put("max_length", maxLength);
        request.put("type", "synopsis");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/synopsis")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error generando sinopsis: " + e.getMessage()));
    }

    /**
     * Sugiere títulos para un libro
     */
    public Mono<String> suggestTitles(String synopsis, String genre, int count) {
        Map<String, Object> request = new HashMap<>();
        request.put("synopsis", synopsis);
        request.put("genre", genre);
        request.put("count", count);
        request.put("type", "title_suggestions");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/titles")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error sugiriendo títulos: " + e.getMessage()));
    }

    /**
     * Genera diálogo para una escena
     */
    public Mono<String> generateDialogue(String scene, String characters, String tone) {
        Map<String, Object> request = new HashMap<>();
        request.put("scene", scene);
        request.put("characters", characters);
        request.put("tone", tone);
        request.put("type", "dialogue");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/dialogue")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error generando diálogo: " + e.getMessage()));
    }

    /**
     * Genera descripción de personaje
     */
    public Mono<String> generateCharacter(String name, String role, String traits) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("role", role);
        request.put("traits", traits);
        request.put("type", "character");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/character")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error generando personaje: " + e.getMessage()));
    }

    /**
     * Mejora un texto existente
     */
    public Mono<String> improveText(String text, String focus) {
        Map<String, Object> request = new HashMap<>();
        request.put("text", text);
        request.put("focus", focus); // grammar, style, clarity, etc.
        request.put("type", "text_improvement");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/generate/improve")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error mejorando texto: " + e.getMessage()));
    }

    /**
     * Analiza el estilo de escritura
     */
    public Mono<String> analyzeWritingStyle(String text) {
        Map<String, Object> request = new HashMap<>();
        request.put("text", text);
        request.put("type", "style_analysis");
        
        return webClient.post()
                .uri(investigatronUrl + "/api/analyze/style")
                .header("Authorization", "Bearer " + investigatronApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error analizando estilo: " + e.getMessage()));
    }
}
