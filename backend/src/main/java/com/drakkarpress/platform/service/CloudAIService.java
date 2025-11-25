package com.drakkarpress.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Servicio para integrar modelos cloud (Deepseek, etc.) con formato genérico de chat/completions.
 * Usa propiedades: ai.provider, ai.model, ai.api.url, ai.api.key.
 */
@Service
@Slf4j
public class CloudAIService {

    @Value("${ai.provider:local}")
    private String providerMode; // 'cloud' para activar

    @Value("${ai.model:llama3.1:8b}")
    private String defaultModel;

    @Value("${ai.api.url:https://YOUR_CLOUD_AI_ENDPOINT}")
    private String apiUrl;

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ollama.timeout:120000}")
    private long timeoutMs; // reutilizamos timeout existente

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CloudAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public boolean isEnabled() {
        return "cloud".equalsIgnoreCase(providerMode) && apiUrl != null && !apiUrl.isBlank();
    }

    public String generate(String prompt) {
        return generate(prompt, defaultModel);
    }

    public String generate(String prompt, String model) {
        if (!isEnabled()) {
            return generateFallback(prompt, "Cloud AI deshabilitado (ai.provider != cloud)");
        }
        if (apiKey == null || apiKey.isBlank()) {
            return generateFallback(prompt, "AI_API_KEY no configurado");
        }
        try {
            // Formato genérico tipo OpenAI / OpenRouter chat
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", new Object[]{ Map.of("role", "system", "content", "Eres un escritor profesional."),
                                              Map.of("role", "user", "content", prompt) },
                    "temperature", 0.7,
                    "top_p", 0.9
            );
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            log.info("Enviando prompt a Cloud AI (modelo: {}): {}", model, prompt.substring(0, Math.min(120, prompt.length())));
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode msg = choices.get(0).get("message");
                    if (msg != null && msg.get("content") != null) {
                        String content = msg.get("content").asText();
                        log.info("Cloud AI generó {} caracteres", content.length());
                        return content;
                    }
                }
                log.warn("Respuesta cloud sin contenido esperado: {}", response.body());
                return generateFallback(prompt, "Formato de respuesta inesperado");
            } else {
                log.error("Cloud AI error {}: {}", response.statusCode(), response.body());
                return generateFallback(prompt, "HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Excepción llamando Cloud AI: {}", e.getMessage(), e);
            return generateFallback(prompt, e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }

    public String generateOutline(String bookTitle, String genre, String synopsis, int numChapters) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Planifica un libro.\n\nTítulo: \"").append(bookTitle).append("\"\n");
        prompt.append("Género: ").append(genre).append("\n");
        prompt.append("Sinopsis: ").append(synopsis).append("\n");
        prompt.append("Número de capítulos: ").append(numChapters).append("\n\n");
        prompt.append("Genera EXACTAMENTE ").append(numChapters).append(" capítulos con formato:\n");
        prompt.append("Capítulo 1: [Título]\n[Resumen 2-3 líneas]\n\n");
        return generate(prompt.toString());
    }

    public String generateChapter(String bookTitle, String genre, String style,
                                  String chapterTitle, String previousChapters, int chapterNumber) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un escritor profesional especializado en ").append(genre).append(".\n\n");
        prompt.append("LIBRO: \"").append(bookTitle).append("\"\n");
        prompt.append("GÉNERO: ").append(genre).append("\n");
        prompt.append("ESTILO: ").append(style).append("\n\n");

        if (previousChapters != null && !previousChapters.isBlank()) {
            prompt.append("=== CONTEXTO NARRATIVO PREVIO ===\n");
            prompt.append("Los capítulos anteriores establecieron:\n");
            prompt.append(previousChapters).append("\n\n");
            prompt.append("IMPORTANTE: Mantén coherencia absoluta con eventos, personajes y detalles anteriores.\n");
            prompt.append("Los personajes deben mantener su personalidad establecida.\n");
            prompt.append("Las líneas temporales deben ser consistentes.\n");
            prompt.append("Respeta las reglas del universo narrativo.\n\n");
        }

        prompt.append("=== CAPÍTULO A ESCRIBIR ===\n");
        prompt.append("Capítulo ").append(chapterNumber).append(": \"").append(chapterTitle).append("\"\n\n");
        
        prompt.append("DIRECTRICES:\n");
        prompt.append("1. Extensión: 800-1200 palabras\n");
        prompt.append("2. Coherencia absoluta con capítulos previos\n");
        prompt.append("3. Desarrollo orgánico de la trama\n");
        prompt.append("4. Descripciones vívidas pero concisas\n");
        prompt.append("5. Diálogos naturales que revelan carácter\n");
        prompt.append("6. Ritmo narrativo apropiado\n");
        prompt.append("7. Flashbacks claramente marcados si los usas\n");
        prompt.append("8. Cada capítulo debe avanzar significativamente\n\n");
        
        prompt.append("Escribe SOLO el contenido del capítulo, sin meta-comentarios.\n\n");

        return generate(prompt.toString());
    }

    public String continueChapter(String currentContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un escritor profesional continuando una escena.\n\n");
        prompt.append("=== CONTENIDO ACTUAL ===\n");
        prompt.append(currentContent);
        prompt.append("\n\n=== INSTRUCCIONES ===\n");
        prompt.append("Continúa esta escena manteniendo:\n");
        prompt.append("1. El mismo tono y voz narrativa\n");
        prompt.append("2. La misma perspectiva (1ª/3ª persona)\n");
        prompt.append("3. Coherencia con lo establecido\n");
        prompt.append("4. Los personajes en carácter\n");
        prompt.append("5. El ritmo y estilo existente\n\n");
        prompt.append("Añade 300-500 palabras nuevas que expanden naturalmente la escena.\n");
        prompt.append("NO repitas lo ya escrito, solo continúa donde quedó.\n");
        prompt.append("Escribe SOLO la continuación, sin introducción.\n\n");
        
        return generate(prompt.toString());
    }

    private String generateFallback(String prompt, String reason) {
        return "[FALLBACK CLOUD AI] Razón: " + reason + "\n\nParte del prompt: " + prompt.substring(0, Math.min(160, prompt.length())) + "...";
    }
}
