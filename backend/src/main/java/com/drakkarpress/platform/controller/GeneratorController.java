package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.BookChapter;
import com.drakkarpress.platform.model.BookProject;
import com.drakkarpress.platform.repository.BookProjectRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.security.JwtTokenProvider;
import com.drakkarpress.platform.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;

@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlatformUserRepository userRepository;
    private final BookProjectRepository projectRepository;

    private String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Token no provisto");
        }
        String token = authHeader.substring(7);
        var userId = jwtTokenProvider.getUserIdFromToken(token);
        return userId.toString();
    }

    // Crear proyecto
    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProject(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                          @RequestBody CreateProjectRequest req) {
        try {
            String userId = extractUserId(authHeader);
            if (!userRepository.existsById(UUID.fromString(userId))) {
                return ResponseEntity.status(401).body(ApiResponse.error("Usuario inválido"));
            }
            BookProject project = generatorService.createProject(userId, req.title(), req.genre(), req.style(), req.synopsis(), req.chapters());
            return ResponseEntity.ok(ApiResponse.ok("Proyecto creado", Map.of(
                    "projectId", project.getId(),
                    "title", project.getTitle()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creando proyecto: " + e.getMessage()));
        }
    }

    // Generar outline
    @PostMapping("/projects/{projectId}/outline")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateOutline(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                            @PathVariable @NonNull UUID projectId) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            var chapters = generatorService.generateOutline(projectId);
            return ResponseEntity.ok(ApiResponse.ok("Outline generado", Map.of(
                    "chapters", chapters.stream().map(ch -> Map.of(
                            "order", ch.getChapterOrder(),
                            "title", ch.getTitle()
                    )).collect(Collectors.toList())
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generando outline: " + e.getMessage()));
        }
    }

    // Generar capítulo
    @PostMapping("/projects/{projectId}/chapters/{chapterOrder}/generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateChapter(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                            @PathVariable @NonNull UUID projectId,
                                                                            @PathVariable Integer chapterOrder,
                                                                            @RequestBody(required = false) GenerateChapterRequest req) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            BookChapter ch = generatorService.generateChapter(projectId, chapterOrder, req == null ? null : req.previousContent());
            return ResponseEntity.ok(ApiResponse.ok("Capítulo generado", Map.of(
                    "chapterOrder", ch.getChapterOrder(),
                    "content", ch.getContent()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generando capítulo: " + e.getMessage()));
        }
    }

    // Continuar capítulo
    @PostMapping("/projects/{projectId}/chapters/{chapterOrder}/continue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> continueChapter(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                            @PathVariable @NonNull UUID projectId,
                                                                            @PathVariable Integer chapterOrder,
                                                                            @RequestBody ContinueChapterRequest req) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            BookChapter ch = generatorService.continueChapter(projectId, chapterOrder, req.currentContent());
            return ResponseEntity.ok(ApiResponse.ok("Capítulo continuado", Map.of(
                    "chapterOrder", ch.getChapterOrder(),
                    "continuation", ch.getContent()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error continuando capítulo: " + e.getMessage()));
        }
    }

    // Actualizar/editar capítulo
    @PutMapping("/projects/{projectId}/chapters/{chapterOrder}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateChapter(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                          @PathVariable @NonNull UUID projectId,
                                                                          @PathVariable Integer chapterOrder,
                                                                          @RequestBody UpdateChapterRequest req) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            BookChapter ch = generatorService.updateChapter(projectId, chapterOrder, req.content());
            return ResponseEntity.ok(ApiResponse.ok("Capítulo actualizado", Map.of(
                    "chapterOrder", ch.getChapterOrder(),
                    "content", ch.getContent()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error actualizando capítulo: " + e.getMessage()));
        }
    }

    // Regenerar capítulo (forzar regeneración manteniendo coherencia)
    @PostMapping("/projects/{projectId}/chapters/{chapterOrder}/regenerate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> regenerateChapter(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                              @PathVariable @NonNull UUID projectId,
                                                                              @PathVariable Integer chapterOrder) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            BookChapter ch = generatorService.regenerateChapter(projectId, chapterOrder);
            return ResponseEntity.ok(ApiResponse.ok("Capítulo regenerado", Map.of(
                    "chapterOrder", ch.getChapterOrder(),
                    "content", ch.getContent(),
                    "subsequentChaptersMarked", "Los capítulos posteriores fueron marcados para revisión"
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error regenerando capítulo: " + e.getMessage()));
        }
    }

    // Regenerar en cascada (regenerar capítulos posteriores)
    @PostMapping("/projects/{projectId}/chapters/{chapterOrder}/regenerate-cascade")
    public ResponseEntity<ApiResponse<Map<String, Object>>> regenerateCascade(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                              @PathVariable @NonNull UUID projectId,
                                                                              @PathVariable Integer chapterOrder) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            var regenerated = generatorService.regenerateCascade(projectId, chapterOrder);
            return ResponseEntity.ok(ApiResponse.ok("Capítulos regenerados en cascada", Map.of(
                    "regeneratedCount", regenerated.size(),
                    "chapters", regenerated.stream().map(ch -> Map.of(
                            "order", ch.getChapterOrder(),
                            "title", ch.getTitle()
                    )).collect(java.util.stream.Collectors.toList())
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error en regeneración cascada: " + e.getMessage()));
        }
    }

    // Generar libro completo automáticamente
    @PostMapping("/projects/{projectId}/generate-complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateComplete(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                             @PathVariable @NonNull UUID projectId) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            BookProject completed = generatorService.generateCompleteBook(projectId);
            long generated = completed.getChapters().stream()
                .filter(ch -> ch.getContent() != null && !ch.getContent().isBlank())
                .count();
            return ResponseEntity.ok(ApiResponse.ok("Libro generado completamente", Map.of(
                    "totalChapters", completed.getPlannedChapters(),
                    "generatedChapters", generated,
                    "title", completed.getTitle()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generando libro completo: " + e.getMessage()));
        }
    }

    // Exportar libro
    @GetMapping("/projects/{projectId}/export")
    public ResponseEntity<ApiResponse<Map<String, Object>>> export(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                    @PathVariable @NonNull UUID projectId) {
        try {
            String userId = extractUserId(authHeader);
            BookProject project = projectRepository.findById(projectId).orElse(null);
            if (project == null || !project.getOwnerUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Acceso denegado"));
            }
            String text = generatorService.exportProject(projectId);
            return ResponseEntity.ok(ApiResponse.ok("Export listo", Map.of(
                    "bookText", text,
                    "title", project.getTitle()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error exportando: " + e.getMessage()));
        }
    }

    // DTOs internos (records para simplicidad)
    public record CreateProjectRequest(String title, String genre, String style, String synopsis, int chapters) {}
    public record GenerateChapterRequest(String previousContent) {}
    public record ContinueChapterRequest(String currentContent) {}
    public record UpdateChapterRequest(String content) {}
}
