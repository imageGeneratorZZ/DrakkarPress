package com.drakkarpress.platform.controller;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.BookGenerationJob;
import com.drakkarpress.platform.model.PublicationJob;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.service.AiBookGenerationService;
// import com.drakkarpress.platform.service.PublicationOrchestrationService;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.drakkarpress.platform.rate.RateLimit;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para generación de libros con IA y publicación automática.
 * 
 * Endpoints:
 * - POST /api/ai/books/generate - Iniciar generación de libro
 * - GET /api/ai/books/jobs/{jobId} - Obtener estado de generación
 * - DELETE /api/ai/books/jobs/{jobId} - Cancelar generación
 * - POST /api/ai/books/{bookId}/publish - Publicar automáticamente
 * - GET /api/ai/books/publications/{jobId} - Obtener estado de publicación
 * - POST /api/ai/books/publications/{jobId}/retry - Reintentar publicación
 */
@RestController
@RequestMapping("/api/ai/books")
@RequiredArgsConstructor
@Slf4j
public class AiBookGenerationController {

    private final AiBookGenerationService generationService;
    // private final PublicationOrchestrationService publicationService;  // NOT IMPLEMENTED
    private final PlatformUserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * Inicia la generación de un libro completo con IA.
     * 
     * POST /api/ai/books/generate
     * Body: {
     *   "prompt": "Escribe una novela de fantasía épica sobre...",
     *   "chapters": 10,
     *   "aiModel": "gpt-4"
     * }
     */
    @PostMapping("/generate")
        @RateLimit(key = "book-generation", limit = 5)
        public ResponseEntity<ApiResponse<BookGenerationJob>> generateBook(
            Authentication authentication,
            @RequestBody GenerateBookRequest request
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Validaciones
            if (request.prompt == null || request.prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("El prompt es requerido"));
            }

            if (request.chapters != null && (request.chapters < 1 || request.chapters > 100)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("El número de capítulos debe estar entre 1 y 100"));
            }

            // Iniciar generación
            BookGenerationJob job = generationService.startBookGeneration(
                    user,
                    request.prompt,
                    request.chapters,
                    request.aiModel
            );

            log.info("🤖 Generación de libro iniciada - User: {} - Job ID: {}", user.getUsername(), job.getId());

                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Generación de libro iniciada. Esto puede tardar varios minutos.", job));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error iniciando generación de libro", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error iniciando generación: " + e.getMessage()));
        }
    }

    /**
     * Obtiene el estado de un job de generación.
     * 
     * GET /api/ai/books/jobs/{jobId}
     */
    @GetMapping("/jobs/{jobId}")
        public ResponseEntity<ApiResponse<JobStatusResponse>> getJobStatus(
            Authentication authentication,
            @PathVariable UUID jobId
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            BookGenerationJob job = generationService.getJobStatus(jobId);

            // Verificar que el usuario sea el dueño del job
            if (!job.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permiso para ver este job"));
            }

            JobStatusResponse response = JobStatusResponse.builder()
                    .jobId(job.getId())
                    .status(job.getStatus().toString())
                    .progressPercentage(job.getProgressPercentage())
                    .currentChapter(job.getCurrentChapter())
                    .targetChapters(job.getTargetChapters())
                    .bookId(job.getBookId())
                    .errorMessage(job.getErrorMessage())
                    .createdAt(job.getCreatedAt())
                    .completedAt(job.getCompletedAt())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo estado de job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo estado: " + e.getMessage()));
        }
    }

    /**
     * Cancela un job de generación en progreso.
     * 
     * DELETE /api/ai/books/jobs/{jobId}
     */
    @DeleteMapping("/jobs/{jobId}")
        public ResponseEntity<ApiResponse<Void>> cancelJob(
            Authentication authentication,
            @PathVariable UUID jobId
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            BookGenerationJob job = generationService.getJobStatus(jobId);

            if (!job.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permiso para cancelar este job"));
            }

            generationService.cancelJob(jobId);
            return ResponseEntity.ok(ApiResponse.success("Job cancelado exitosamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error cancelando job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error cancelando job: " + e.getMessage()));
        }
    }

    /**
     * Publica un libro automáticamente en plataformas externas.
     * 
     * POST /api/ai/books/{bookId}/publish
     * Body: {
     *   "platforms": ["KDP", "GOOGLE_PLAY", "LULU"]
     * }
     */
    
    /* NOT_IMPLEMENTED - PublicationOrchestrationService disabled
    @PostMapping("/{bookId}/publish")
        @RateLimit(key = "book-publication", limit = 20)
        public ResponseEntity<ApiResponse<PublicationJob>> publishBook(
            Authentication authentication,
            @PathVariable UUID bookId,
            @RequestBody PublishBookRequest request
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

            // Validar que el usuario sea el autor
            if (!book.getAuthor().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permiso para publicar este libro"));
            }

            // Validar plataformas
            List<String> validPlatforms = Arrays.asList("KDP", "GOOGLE_PLAY", "GOOGLE", "LULU");
            if (request.platforms == null || request.platforms.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Debes especificar al menos una plataforma"));
            }

            for (String platform : request.platforms) {
                if (!validPlatforms.contains(platform.toUpperCase())) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Plataforma inválida: " + platform));
                }
            }

            // Iniciar publicación
            PublicationJob job = publicationService.startPublication(book, user, request.platforms);

            log.info("🚀 Publicación automática iniciada - Book ID: {} - Platforms: {}", bookId, request.platforms);

                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Publicación iniciada. El proceso puede tardar varios minutos.", job));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error iniciando publicación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error iniciando publicación: " + e.getMessage()));
        }
    }

    @GetMapping("/publications/{jobId}")
        public ResponseEntity<ApiResponse<PublicationStatusResponse>> getPublicationStatus(
            Authentication authentication,
            @PathVariable UUID jobId
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            PublicationJob job = publicationService.getJobStatus(jobId);

            if (!job.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permiso para ver este job"));
            }

            PublicationStatusResponse response = PublicationStatusResponse.builder()
                    .jobId(job.getId())
                    .bookId(job.getBook().getId())
                    .status(job.getStatus().toString())
                    .platformStatuses(job.getPlatformStatuses())
                    .kdpAsin(job.getKdpAsin())
                    .googlePlayId(job.getGooglePlayId())
                    .luluProjectId(job.getLuluProjectId())
                    .errorMessage(job.getErrorMessage())
                    .createdAt(job.getCreatedAt())
                    .completedAt(job.getCompletedAt())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo estado de publicación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo estado: " + e.getMessage()));
        }
    }

    @PostMapping("/publications/{jobId}/retry")
        @RateLimit(key = "book-publication-retry", limit = 10)
        public ResponseEntity<ApiResponse<PublicationJob>> retryPublication(
            Authentication authentication,
            @PathVariable UUID jobId
        ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof com.drakkarpress.platform.security.JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("No autenticado"));
            }
            UUID userId = principal.userId();
            PublicationJob job = publicationService.getJobStatus(jobId);

            if (!job.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permiso para reintentar este job"));
            }

            PublicationJob retriedJob = publicationService.retryPublication(jobId);
            return ResponseEntity.ok(ApiResponse.success("Reintentando publicación...", retriedJob));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error reintentando publicación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error reintentando publicación: " + e.getMessage()));
        }
    }
    */

    // ========================== DTOs ==========================

    public static class GenerateBookRequest {
        public String prompt;
        public Integer chapters;
        public String aiModel;
    }

    public static class PublishBookRequest {
        public List<String> platforms;
    }

    @lombok.Builder
    @lombok.Data
    public static class JobStatusResponse {
        private UUID jobId;
        private String status;
        private Integer progressPercentage;
        private Integer currentChapter;
        private Integer targetChapters;
        private UUID bookId;
        private String errorMessage;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime completedAt;
    }

    @lombok.Builder
    @lombok.Data
    public static class PublicationStatusResponse {
        private UUID jobId;
        private UUID bookId;
        private String status;
        private String platformStatuses;  // JSON
        private String kdpAsin;
        private String googlePlayId;
        private String luluProjectId;
        private String errorMessage;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime completedAt;
    }
}
