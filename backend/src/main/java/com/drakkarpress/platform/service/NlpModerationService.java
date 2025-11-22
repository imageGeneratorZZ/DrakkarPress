package com.drakkarpress.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de análisis NLP externo para moderación de contenido
 * Integra con APIs de clasificación de texto (OpenAI Moderation, Perspective API, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NlpModerationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.moderation.nlp.api-url:}")
    private String nlpApiUrl;

    @Value("${app.moderation.nlp.api-key:}")
    private String nlpApiKey;

    /**
     * Analiza texto usando NLP externo y retorna scores de moderación
     */
    public Map<String, Double> analyzeText(String text) {
        if (nlpApiUrl == null || nlpApiUrl.isEmpty()) {
            log.warn("NLP API not configured, using fallback heuristic scoring");
            return fallbackHeuristicScoring(text);
        }

        try {
            // Integración con API externa (OpenAI Moderation, Perspective API, etc.)
            WebClient webClient = webClientBuilder.baseUrl(nlpApiUrl).build();

            Map<String, Object> request = new HashMap<>();
            request.put("text", text);

            Map<String, Double> response = webClient.post()
                    .uri("/analyze")
                    .header("Authorization", "Bearer " + nlpApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(e -> {
                        log.error("Error calling NLP API, using fallback", e);
                        return Mono.just(fallbackHeuristicScoring(text));
                    })
                    .block();

            return response != null ? response : fallbackHeuristicScoring(text);

        } catch (Exception e) {
            log.error("Exception in NLP analysis, using fallback", e);
            return fallbackHeuristicScoring(text);
        }
    }

    /**
     * Scoring heurístico de fallback cuando API externa no está disponible
     */
    private Map<String, Double> fallbackHeuristicScoring(String text) {
        Map<String, Double> scores = new HashMap<>();
        
        String lowerText = text.toLowerCase();

        // CSAM detection (palabras clave ultra sensibles)
        String[] csamKeywords = {"child", "kid", "minor", "underage", "teen", "young", "little"};
        String[] sexualKeywords = {"sex", "nude", "explicit", "porn", "naked"};
        double csamScore = 0.0;
        for (String csam : csamKeywords) {
            for (String sexual : sexualKeywords) {
                if (lowerText.contains(csam) && lowerText.contains(sexual)) {
                    csamScore = 1.0; // Máxima alerta
                    break;
                }
            }
        }
        scores.put("csam", csamScore);

        // Hate speech
        String[] hateKeywords = {"hate", "kill", "death", "nazi", "racist", "f***", "n***"};
        double hateScore = 0.0;
        for (String hate : hateKeywords) {
            if (lowerText.contains(hate)) {
                hateScore += 0.3;
            }
        }
        scores.put("hate_speech", Math.min(hateScore, 1.0));

        // Violence
        String[] violenceKeywords = {"kill", "murder", "attack", "weapon", "bomb", "terror"};
        double violenceScore = 0.0;
        for (String viol : violenceKeywords) {
            if (lowerText.contains(viol)) {
                violenceScore += 0.25;
            }
        }
        scores.put("violence", Math.min(violenceScore, 1.0));

        // Spam
        double spamScore = lowerText.contains("buy now") || lowerText.contains("click here") ? 0.6 : 0.1;
        scores.put("spam", spamScore);

        // Overall toxicity
        double toxicity = (csamScore * 2 + hateScore + violenceScore + spamScore) / 5.0;
        scores.put("toxicity", Math.min(toxicity, 1.0));

        return scores;
    }

    /**
     * Determina si el contenido debe bloquearse basado en scores
     */
    public boolean shouldBlock(Map<String, Double> scores) {
        // Bloqueo inmediato si hay CSAM
        if (scores.getOrDefault("csam", 0.0) > 0.5) {
            return true;
        }

        // Bloqueo si toxicidad general es muy alta
        if (scores.getOrDefault("toxicity", 0.0) > 0.8) {
            return true;
        }

        // Bloqueo si hate speech es severo
        if (scores.getOrDefault("hate_speech", 0.0) > 0.85) {
            return true;
        }

        return false;
    }

    /**
     * Determina si el contenido requiere revisión humana
     */
    public boolean requiresHumanReview(Map<String, Double> scores) {
        // Revisión humana si CSAM score es sospechoso pero no definitivo
        if (scores.getOrDefault("csam", 0.0) > 0.3) {
            return true;
        }

        // Revisión si toxicidad es moderada-alta
        if (scores.getOrDefault("toxicity", 0.0) > 0.6) {
            return true;
        }

        return false;
    }
}
