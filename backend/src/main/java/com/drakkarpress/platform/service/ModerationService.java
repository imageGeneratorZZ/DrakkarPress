package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.ModerationFlag;
import com.drakkarpress.platform.repository.ModerationFlagRepository;
import com.drakkarpress.platform.repository.ReelRepository;
import com.drakkarpress.platform.repository.StoryRepository;
import com.drakkarpress.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Servicio de moderación integrado con Hash Matching y NLP externo
 * Pipeline: Hash Check → NLP Analysis → Rule Engine → Human Review Queue
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationService {

    private final ModerationFlagRepository flagRepository;
    private final HashMatchingService hashMatchingService;
    private final NlpModerationService nlpModerationService;
    private final BookRepository bookRepository;
    private final ReelRepository reelRepository;
    private final StoryRepository storyRepository;
    private final ObjectMapper objectMapper;

    /**
     * Analiza contenido completo (texto + media) usando pipeline de moderación
     */
    @Transactional
    public ModerationFlag analyzeAndFlag(ModerationFlag.ResourceType type, UUID resourceId, 
                                          String text, String mediaUrl, byte[] imageData) {
        log.info("Starting moderation analysis for {} {}", type, resourceId);

        try {
            // PASO 1: Hash matching (si hay imagen)
            boolean hashMatch = false;
            if (imageData != null && imageData.length > 0) {
                hashMatch = hashMatchingService.checkImageHash(imageData);
                if (hashMatch) {
                    log.error("CRITICAL: Hash match detected for {} {} - Blocking immediately", type, resourceId);
                    return createBlockedFlag(type, resourceId, 
                            "{\"hash_match\":true,\"csam\":1.0,\"auto_action\":\"BLOCKED\"}", 
                            "Hash match with prohibited content database");
                }
            }

            // PASO 2: NLP analysis (si hay texto)
            Map<String, Double> nlpScores = null;
            if (text != null && !text.isEmpty()) {
                nlpScores = nlpModerationService.analyzeText(text);
                
                // Bloqueo automático si NLP detecta contenido crítico
                if (nlpModerationService.shouldBlock(nlpScores)) {
                    log.warn("NLP auto-block triggered for {} {}", type, resourceId);
                    String scoresJson = objectMapper.writeValueAsString(nlpScores);
                    return createBlockedFlag(type, resourceId, scoresJson, 
                            "NLP detected prohibited content");
                }

                // Revisión humana si hay riesgo moderado
                if (nlpModerationService.requiresHumanReview(nlpScores)) {
                    log.info("Content flagged for human review: {} {}", type, resourceId);
                    String scoresJson = objectMapper.writeValueAsString(nlpScores);
                    return createPendingFlag(type, resourceId, scoresJson, 
                            "Flagged for human review");
                }
            }

            // PASO 3: Contenido seguro, aprobar
            String scoresJson = nlpScores != null ? 
                    objectMapper.writeValueAsString(nlpScores) : "{\"auto_approved\":true}";
            @SuppressWarnings("null")
            ModerationFlag flag = ModerationFlag.builder()
                    .resourceType(type)
                    .resourceId(resourceId)
                    .status(ModerationFlag.Status.APPROVED)
                    .scoresJson(scoresJson)
                    .build();
            flagRepository.save(flag);

            // Actualizar safetyStatus del recurso
            updateResourceSafetyStatus(type, resourceId, "SAFE");

            log.info("Content approved: {} {}", type, resourceId);
            return flag;

        } catch (Exception e) {
            log.error("Error in moderation analysis", e);
            // Fail-safe: bloquear si hay error
            return createBlockedFlag(type, resourceId, 
                    "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}", 
                    "Error during analysis - fail-safe block");
        }
    }

    /**
     * Análisis asíncrono para no bloquear el request principal
     */
    @Async
    public void analyzeAsync(ModerationFlag.ResourceType type, UUID resourceId, 
                             String text, String mediaUrl, byte[] imageData) {
        analyzeAndFlag(type, resourceId, text, mediaUrl, imageData);
    }

    @SuppressWarnings("null")
    private ModerationFlag createBlockedFlag(ModerationFlag.ResourceType type, UUID resourceId, 
                                              String scoresJson, String reason) {
        ModerationFlag flag = ModerationFlag.builder()
                .resourceType(type)
                .resourceId(resourceId)
                .status(ModerationFlag.Status.AUTO_BLOCKED)
                .scoresJson(scoresJson)
                .finalDecision(ModerationFlag.FinalDecision.BLOCKED)
                .reviewerNotes(reason)
                .build();
        flagRepository.save(flag);
        
        // Actualizar safetyStatus del recurso
        updateResourceSafetyStatus(type, resourceId, "BLOCKED");
        
        return flag;
    }

    @SuppressWarnings("null")
    private ModerationFlag createPendingFlag(ModerationFlag.ResourceType type, UUID resourceId, 
                                              String scoresJson, String reason) {
        ModerationFlag flag = ModerationFlag.builder()
                .resourceType(type)
                .resourceId(resourceId)
                .status(ModerationFlag.Status.PENDING)
                .scoresJson(scoresJson)
                .reviewerNotes(reason)
                .build();
        flagRepository.save(flag);
        
        // Actualizar safetyStatus del recurso
        updateResourceSafetyStatus(type, resourceId, "REVIEW");
        
        return flag;
    }

    /**
     * Actualiza el safetyStatus en el recurso correspondiente
     */
    @Transactional
    @SuppressWarnings("null")
    public void updateResourceSafetyStatus(ModerationFlag.ResourceType type, UUID resourceId, String status) {
        try {
            switch (type) {
                case BOOK:
                    bookRepository.findById(resourceId).ifPresent(book -> {
                        book.setSafetyStatus(status);
                        bookRepository.save(book);
                    });
                    break;
                case REEL:
                    reelRepository.findById(resourceId).ifPresent(reel -> {
                        // Reel no tiene safetyStatus aún, agregar si es necesario
                        log.debug("Reel safety status updated: {} -> {}", resourceId, status);
                    });
                    break;
                case STORY:
                    storyRepository.findById(resourceId).ifPresent(story -> {
                        // Story no tiene safetyStatus aún, agregar si es necesario
                        log.debug("Story safety status updated: {} -> {}", resourceId, status);
                    });
                    break;
                default:
                    log.warn("Unknown resource type for safety status update: {}", type);
            }
        } catch (Exception e) {
            log.error("Error updating resource safety status", e);
        }
    }

    /**
     * Revisión manual por moderador humano
     */
    @Transactional
    @SuppressWarnings("null")
    public void humanReview(UUID flagId, ModerationFlag.Status decision, String notes) {
        ModerationFlag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Flag not found"));
        
        flag.setStatus(decision);
        flag.setReviewerNotes(notes);
        flag.setFinalDecision(decision == ModerationFlag.Status.APPROVED ? 
                ModerationFlag.FinalDecision.SAFE : ModerationFlag.FinalDecision.BLOCKED);
        flagRepository.save(flag);

        // Actualizar safetyStatus según decisión
        String safetyStatus = decision == ModerationFlag.Status.APPROVED ? "SAFE" : "BLOCKED";
        updateResourceSafetyStatus(flag.getResourceType(), flag.getResourceId(), safetyStatus);

        log.info("Human review completed: flag={} decision={}", flagId, decision);
    }
}
