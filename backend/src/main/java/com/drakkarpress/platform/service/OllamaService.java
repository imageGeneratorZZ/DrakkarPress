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

@Service
@Slf4j
public class OllamaService {

    @Value("${ollama.api.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.model:llama3.1:8b}")
    private String defaultModel;

    @Value("${ollama.timeout:120000}")
    private long timeoutMs;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OllamaService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Verifica si Ollama está disponible
     */
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaUrl + "/api/tags"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            log.warn("Ollama no disponible en {}: {}", ollamaUrl, e.getMessage());
            return false;
        }
    }

    /**
     * Genera texto usando Ollama
     */
    public String generate(String prompt) {
        return generate(prompt, defaultModel);
    }

    /**
     * Genera texto usando un modelo específico
     */
    public String generate(String prompt, String model) {
        if (!isAvailable()) {
            log.error("Ollama no disponible. Usando contenido de fallback.");
            return generateFallback(prompt);
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "prompt", prompt,
                    "stream", false,
                    "options", Map.of(
                            "temperature", 0.7,
                            "top_p", 0.9,
                            "max_tokens", 2000
                    )
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaUrl + "/api/generate"))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            log.info("Enviando prompt a Ollama (modelo: {}): {}", model, prompt.substring(0, Math.min(100, prompt.length())));

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                String generatedText = jsonResponse.get("response").asText();
                log.info("Ollama generó {} caracteres", generatedText.length());
                return generatedText;
            } else {
                log.error("Ollama error: {} - {}", response.statusCode(), response.body());
                return generateFallback(prompt);
            }

        } catch (Exception e) {
            log.error("Error llamando a Ollama: {}", e.getMessage(), e);
            return generateFallback(prompt);
        }
    }

    /**
     * Genera contenido con contexto estructurado (para libros)
     */
    public String generateChapter(String bookTitle, String genre, String style, 
                                   String chapterTitle, String previousChapters, int chapterNumber) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un escritor profesional especializado en ").append(genre).append(".\n\n");
        prompt.append("LIBRO: \"").append(bookTitle).append("\"\n");
        prompt.append("GÉNERO: ").append(genre).append("\n");
        prompt.append("ESTILO: ").append(style).append("\n\n");

        if (previousChapters != null && !previousChapters.isEmpty()) {
            prompt.append("=== CONTEXTO NARRATIVO PREVIO ===\n");
            prompt.append("Los capítulos anteriores establecieron los siguientes elementos:\n");
            prompt.append(previousChapters).append("\n\n");
            prompt.append("IMPORTANTE: Mantén absoluta coherencia con los eventos, personajes y detalles establecidos arriba.\n");
            prompt.append("Los personajes deben mantener su personalidad y motivaciones.\n");
            prompt.append("Las líneas temporales deben ser consistentes.\n");
            prompt.append("Respeta cualquier regla o sistema establecido en el universo narrativo.\n\n");
        }

        prompt.append("=== CAPÍTULO A ESCRIBIR ===\n");
        prompt.append("Capítulo ").append(chapterNumber).append(": \"").append(chapterTitle).append("\"\n\n");
        
        prompt.append("DIRECTRICES DE ESCRITURA:\n");
        prompt.append("1. Extensión: 800-1200 palabras\n");
        prompt.append("2. Mantén coherencia absoluta con capítulos previos\n");
        prompt.append("3. Desarrolla la trama de forma orgánica y natural\n");
        prompt.append("4. Usa descripciones vívidas pero concisas\n");
        prompt.append("5. Los diálogos deben sonar naturales y revelar carácter\n");
        prompt.append("6. Mantén el ritmo narrativo apropiado para el género\n");
        prompt.append("7. Si usas flashbacks, márcalos claramente pero mantén la coherencia temporal\n");
        prompt.append("8. Cada capítulo debe avanzar la historia de forma significativa\n\n");
        
        prompt.append("FORMATO DE RESPUESTA:\n");
        prompt.append("Escribe SOLO el contenido del capítulo, sin meta-comentarios ni explicaciones.\n");
        prompt.append("No incluyas frases como 'Aquí está el capítulo' o 'Espero que te guste'.\n");
        prompt.append("Comienza directamente con la narrativa.\n\n");
        
        prompt.append("--- CAPÍTULO ").append(chapterNumber).append(" ---\n\n");

        return generate(prompt.toString());
    }

    /**
     * Genera un outline de capítulos
     */
    public String generateOutline(String bookTitle, String genre, String synopsis, int numChapters) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un escritor profesional planificando un libro.\n\n");
        prompt.append("Título: \"").append(bookTitle).append("\"\n");
        prompt.append("Género: ").append(genre).append("\n");
        prompt.append("Sinopsis: ").append(synopsis).append("\n");
        prompt.append("Número de capítulos: ").append(numChapters).append("\n\n");
        prompt.append("Genera un outline estructurado con exactamente ").append(numChapters).append(" capítulos.\n");
        prompt.append("Para cada capítulo proporciona:\n");
        prompt.append("- Número del capítulo\n");
        prompt.append("- Título del capítulo\n");
        prompt.append("- Breve resumen (2-3 líneas)\n\n");
        prompt.append("Formato:\n");
        prompt.append("Capítulo 1: [Título]\n[Resumen]\n\n");

        return generate(prompt.toString());
    }

    /**
     * Continúa un capítulo existente
     */
    public String continueChapter(String currentContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un escritor profesional continuando una escena.\n\n");
        prompt.append("Contenido actual:\n");
        prompt.append(currentContent).append("\n\n");
        prompt.append("Continúa la narrativa de forma natural y coherente. ");
        prompt.append("Escribe entre 300-500 palabras adicionales. ");
        prompt.append("Mantén el estilo, tono y perspectiva narrativa del texto anterior.\n\n");

        return generate(prompt.toString());
    }

    /**
     * Fallback cuando Ollama no está disponible
     */
    private String generateFallback(String prompt) {
        log.warn("Usando contenido de fallback");
        return "[DEMO MODE - Ollama no disponible]\n\n" +
               "Este es contenido generado automáticamente de demostración.\n\n" +
               "Para habilitar generación real con IA:\n" +
               "1. Instala Ollama desde https://ollama.com/download\n" +
               "2. Descarga un modelo: ollama pull llama3.1:8b\n" +
               "3. Reinicia el backend\n\n" +
               "Prompt recibido: " + prompt.substring(0, Math.min(200, prompt.length())) + "...";
    }
}
