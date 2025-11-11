package com.drakkarpress.controller;

import com.drakkarpress.service.AiGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiGeneratorController {

    @Autowired
    private AiGeneratorService aiGeneratorService;

    /**
     * Generar idea de libro
     * POST /api/ai/generate-idea
     * Body: { "genre": "Romance", "keywords": "amor, pasión, verano" }
     */
    @PostMapping("/generate-idea")
    public Mono<ResponseEntity<String>> generateBookIdea(@RequestBody Map<String, String> request) {
        String genre = request.getOrDefault("genre", "General");
        String keywords = request.getOrDefault("keywords", "");
        
        return aiGeneratorService.generateBookIdea(genre, keywords)
                .map(ResponseEntity::ok);
    }

    /**
     * Extender capítulo
     * POST /api/ai/extend-chapter
     * Body: { 
     *   "currentText": "...", 
     *   "direction": "continue|twist|climax", 
     *   "words": 500 
     * }
     */
    @PostMapping("/extend-chapter")
    public Mono<ResponseEntity<String>> extendChapter(@RequestBody Map<String, Object> request) {
        String currentText = (String) request.getOrDefault("currentText", "");
        String direction = (String) request.getOrDefault("direction", "continue");
        int words = (int) request.getOrDefault("words", 300);
        
        return aiGeneratorService.extendChapter(currentText, direction, words)
                .map(ResponseEntity::ok);
    }

    /**
     * Generar sinopsis
     * POST /api/ai/generate-synopsis
     * Body: { "bookContent": "...", "maxLength": 200 }
     */
    @PostMapping("/generate-synopsis")
    public Mono<ResponseEntity<String>> generateSynopsis(@RequestBody Map<String, Object> request) {
        String bookContent = (String) request.getOrDefault("bookContent", "");
        int maxLength = (int) request.getOrDefault("maxLength", 150);
        
        return aiGeneratorService.generateSynopsis(bookContent, maxLength)
                .map(ResponseEntity::ok);
    }

    /**
     * Sugerir títulos
     * POST /api/ai/suggest-titles
     * Body: { "synopsis": "...", "genre": "Romance", "count": 5 }
     */
    @PostMapping("/suggest-titles")
    public Mono<ResponseEntity<String>> suggestTitles(@RequestBody Map<String, Object> request) {
        String synopsis = (String) request.getOrDefault("synopsis", "");
        String genre = (String) request.getOrDefault("genre", "General");
        int count = (int) request.getOrDefault("count", 5);
        
        return aiGeneratorService.suggestTitles(synopsis, genre, count)
                .map(ResponseEntity::ok);
    }

    /**
     * Generar diálogo
     * POST /api/ai/generate-dialogue
     * Body: { 
     *   "scene": "Reunión en café", 
     *   "characters": "Ana y Carlos", 
     *   "tone": "romántico" 
     * }
     */
    @PostMapping("/generate-dialogue")
    public Mono<ResponseEntity<String>> generateDialogue(@RequestBody Map<String, String> request) {
        String scene = request.getOrDefault("scene", "");
        String characters = request.getOrDefault("characters", "");
        String tone = request.getOrDefault("tone", "neutral");
        
        return aiGeneratorService.generateDialogue(scene, characters, tone)
                .map(ResponseEntity::ok);
    }

    /**
     * Generar personaje
     * POST /api/ai/generate-character
     * Body: { 
     *   "name": "María", 
     *   "role": "protagonista", 
     *   "traits": "valiente, inteligente" 
     * }
     */
    @PostMapping("/generate-character")
    public Mono<ResponseEntity<String>> generateCharacter(@RequestBody Map<String, String> request) {
        String name = request.getOrDefault("name", "");
        String role = request.getOrDefault("role", "");
        String traits = request.getOrDefault("traits", "");
        
        return aiGeneratorService.generateCharacter(name, role, traits)
                .map(ResponseEntity::ok);
    }

    /**
     * Mejorar texto
     * POST /api/ai/improve-text
     * Body: { 
     *   "text": "...", 
     *   "focus": "grammar|style|clarity" 
     * }
     */
    @PostMapping("/improve-text")
    public Mono<ResponseEntity<String>> improveText(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "");
        String focus = request.getOrDefault("focus", "general");
        
        return aiGeneratorService.improveText(text, focus)
                .map(ResponseEntity::ok);
    }

    /**
     * Analizar estilo de escritura
     * POST /api/ai/analyze-style
     * Body: { "text": "..." }
     */
    @PostMapping("/analyze-style")
    public Mono<ResponseEntity<String>> analyzeWritingStyle(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "");
        
        return aiGeneratorService.analyzeWritingStyle(text)
                .map(ResponseEntity::ok);
    }

    /**
     * Estado del servicio de IA
     * GET /api/ai/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAiStatus() {
        return ResponseEntity.ok(Map.of(
            "service", "AI Generator Service",
            "status", "active",
            "features", new String[]{
                "generate-idea",
                "extend-chapter",
                "generate-synopsis",
                "suggest-titles",
                "generate-dialogue",
                "generate-character",
                "improve-text",
                "analyze-style"
            }
        ));
    }
}
