package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.BookChapter;
import com.drakkarpress.platform.model.BookProject;
import com.drakkarpress.platform.repository.BookChapterRepository;
import com.drakkarpress.platform.repository.BookProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorService {

    private final BookProjectRepository projectRepository;
    private final BookChapterRepository chapterRepository;
    private final OllamaService ollamaService;
    private final CloudAIService cloudAIService;

    @Value("${ai.model:llama3.1:8b}")
    private String aiModel;

    @Transactional
    public BookProject createProject(String userId, String title, String genre, String style, String synopsis, int chapters) {
        BookProject project = BookProject.builder()
                .ownerUserId(userId)
                .title(title)
                .genre(genre)
                .style(style)
                .synopsis(synopsis)
                .plannedChapters(chapters)
                .outlineGenerated(false)
                .build();
        return projectRepository.save(project);
    }

    @Transactional
    public List<BookChapter> generateOutline(@NonNull UUID projectId) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        if (Boolean.TRUE.equals(project.getOutlineGenerated()) && !project.getChapters().isEmpty()) {
            return project.getChapters();
        }

        // Generar outline con Ollama
        log.info("Generando outline para proyecto: {} ({})", project.getTitle(), projectId);
        String outlineText;
        if (cloudAIService.isEnabled() && aiModel.endsWith(":cloud")) {
            outlineText = cloudAIService.generateOutline(
                project.getTitle(),
                project.getGenre(),
                project.getSynopsis(),
                project.getPlannedChapters()
            );
        } else {
            outlineText = ollamaService.generateOutline(
                project.getTitle(),
                project.getGenre(),
                project.getSynopsis(),
                project.getPlannedChapters()
            );
        }

        // Parse del outline generado para extraer títulos
        List<BookChapter> chapters = parseOutline(project, outlineText);

        // Si no se pudo parsear o no hay suficientes capítulos, usar fallback
        if (chapters.size() < project.getPlannedChapters()) {
            log.warn("Outline generado incompleto, usando fallback");
            chapters.clear();
            for (int i = 1; i <= project.getPlannedChapters(); i++) {
                BookChapter ch = BookChapter.builder()
                        .project(project)
                        .chapterOrder(i)
                        .title("Capítulo " + i)
                        .content("")
                        .build();
                chapters.add(ch);
            }
        }

        project.setOutlineGenerated(true);
        project.getChapters().clear();
        project.getChapters().addAll(chapters);
        projectRepository.save(project);
        return chapters;
    }

    /**
     * Parsea el outline generado por IA para extraer títulos de capítulos
     */
    private List<BookChapter> parseOutline(BookProject project, String outlineText) {
        List<BookChapter> chapters = new ArrayList<>();
        Pattern pattern = Pattern.compile("Cap[íi]tulo\\s+(\\d+):\\s*([^\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(outlineText);

        while (matcher.find() && chapters.size() < project.getPlannedChapters()) {
            int order = Integer.parseInt(matcher.group(1));
            String title = matcher.group(2).trim();
            
            BookChapter ch = BookChapter.builder()
                    .project(project)
                    .chapterOrder(order)
                    .title(title)
                    .content("")
                    .build();
            chapters.add(ch);
        }

        return chapters;
    }

    @Transactional
    public BookChapter generateChapter(@NonNull UUID projectId, int chapterOrder, String previousContent) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, chapterOrder)
                .orElseThrow(() -> new IllegalArgumentException("Capítulo no existe en proyecto"));
        
        if (chapter.getContent() != null && !chapter.getContent().isBlank()) {
            return chapter; // ya generado
        }

        // Recopilar contexto de capítulos anteriores
        String context = buildPreviousContext(project, chapterOrder);

        // Generar contenido con Ollama
        log.info("Generando capítulo {} para proyecto: {}", chapterOrder, project.getTitle());
        String generatedContent;
        if (cloudAIService.isEnabled() && aiModel.endsWith(":cloud")) {
            generatedContent = cloudAIService.generateChapter(
                project.getTitle(),
                project.getGenre(),
                project.getStyle(),
                chapter.getTitle(),
                context,
                chapterOrder
            );
        } else {
            generatedContent = ollamaService.generateChapter(
                project.getTitle(),
                project.getGenre(),
                project.getStyle(),
                chapter.getTitle(),
                context,
                chapterOrder
            );
        }

        chapter.setContent(generatedContent);
        chapter.setStatus(BookChapter.ChapterStatus.GENERATED);
        return chapterRepository.save(chapter);
    }

    /**
     * Construye contexto de capítulos anteriores (máximo 2 capítulos previos)
     */
    private String buildPreviousContext(BookProject project, int currentChapter) {
        if (currentChapter <= 1) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        int startChapter = Math.max(1, currentChapter - 2);

        for (int i = startChapter; i < currentChapter; i++) {
            final int chapterNum = i; // Variable final para lambda
            chapterRepository.findByProjectAndChapterOrder(project, i).ifPresent(ch -> {
                if (ch.getContent() != null && !ch.getContent().isBlank()) {
                    String summary = ch.getContent().length() > 300 
                        ? ch.getContent().substring(0, 300) + "..." 
                        : ch.getContent();
                    context.append("Capítulo ").append(chapterNum).append(" (resumen): ").append(summary).append("\n\n");
                }
            });
        }

        return context.toString();
    }

    @Transactional
    public BookChapter continueChapter(@NonNull UUID projectId, int chapterOrder, String currentContent) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, chapterOrder)
                .orElseThrow(() -> new IllegalArgumentException("Capítulo no existe en proyecto"));
        
        // Generar continuación con Ollama
        log.info("Continuando capítulo {} para proyecto: {}", chapterOrder, project.getTitle());
        String continuation = (cloudAIService.isEnabled() && aiModel.endsWith(":cloud"))
            ? cloudAIService.continueChapter(currentContent)
            : ollamaService.continueChapter(currentContent);

        chapter.setContent((currentContent == null ? "" : currentContent) + "\n\n" + continuation);
        chapter.setStatus(BookChapter.ChapterStatus.EDITED);
        return chapterRepository.save(chapter);
    }

    @Transactional
    public BookChapter updateChapter(@NonNull UUID projectId, int chapterOrder, String newContent) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, chapterOrder)
                .orElseThrow(() -> new IllegalArgumentException("Capítulo no existe en proyecto"));
        chapter.setContent(newContent);
        chapter.setStatus(BookChapter.ChapterStatus.EDITED);
        return chapterRepository.save(chapter);
    }

    /**
     * Regenera un capítulo manteniendo coherencia con capítulos anteriores
     * y marcando capítulos posteriores como que necesitan regeneración
     */
    @Transactional
    public BookChapter regenerateChapter(@NonNull UUID projectId, int chapterOrder) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, chapterOrder)
                .orElseThrow(() -> new IllegalArgumentException("Capítulo no existe en proyecto"));
        
        // Limpiar contenido actual
        chapter.setContent("");
        chapter.setStatus(BookChapter.ChapterStatus.PENDING);
        
        // Construir contexto de capítulos anteriores
        String context = buildPreviousContext(project, chapterOrder);
        
        // Generar nuevo contenido
        log.info("Regenerando capítulo {} para proyecto: {}", chapterOrder, project.getTitle());
        String generatedContent;
        if (cloudAIService.isEnabled() && aiModel.endsWith(":cloud")) {
            generatedContent = cloudAIService.generateChapter(
                project.getTitle(),
                project.getGenre(),
                project.getStyle(),
                chapter.getTitle(),
                context,
                chapterOrder
            );
        } else {
            generatedContent = ollamaService.generateChapter(
                project.getTitle(),
                project.getGenre(),
                project.getStyle(),
                chapter.getTitle(),
                context,
                chapterOrder
            );
        }
        
        chapter.setContent(generatedContent);
        chapter.setStatus(BookChapter.ChapterStatus.GENERATED);
        BookChapter saved = chapterRepository.save(chapter);
        
        // Marcar capítulos posteriores como que necesitan revisión
        markSubsequentChaptersForReview(project, chapterOrder);
        
        return saved;
    }
    
    /**
     * Marca capítulos posteriores como que necesitan revisión debido a cambios en capítulos previos
     */
    private void markSubsequentChaptersForReview(BookProject project, int fromChapter) {
        project.getChapters().stream()
            .filter(ch -> ch.getChapterOrder() > fromChapter)
            .filter(ch -> ch.getContent() != null && !ch.getContent().isBlank())
            .forEach(ch -> {
                ch.setStatus(BookChapter.ChapterStatus.NEEDS_REVIEW);
                chapterRepository.save(ch);
            });
    }
    
    /**
     * Genera automáticamente todo el libro desde una idea
     */
    @Transactional
    public BookProject generateCompleteBook(@NonNull UUID projectId) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        
        // Generar outline si no existe
        if (!Boolean.TRUE.equals(project.getOutlineGenerated()) || project.getChapters().isEmpty()) {
            generateOutline(projectId);
        }
        
        // Generar todos los capítulos en orden
        for (int i = 1; i <= project.getPlannedChapters(); i++) {
            try {
                BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, i)
                    .orElse(null);
                if (chapter != null && (chapter.getContent() == null || chapter.getContent().isBlank())) {
                    generateChapter(projectId, i, null);
                }
            } catch (Exception e) {
                log.error("Error generando capítulo {}: {}", i, e.getMessage());
            }
        }
        
        return projectRepository.findById(projectId).orElse(project);
    }
    
    /**
     * Regenera capítulos posteriores en cascada para mantener coherencia narrativa
     */
    @Transactional
    public List<BookChapter> regenerateCascade(@NonNull UUID projectId, int fromChapter) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        
        List<BookChapter> regenerated = new ArrayList<>();
        
        for (int i = fromChapter + 1; i <= project.getPlannedChapters(); i++) {
            try {
                BookChapter chapter = chapterRepository.findByProjectAndChapterOrder(project, i)
                    .orElse(null);
                if (chapter != null && chapter.getContent() != null && !chapter.getContent().isBlank()) {
                    BookChapter regen = regenerateChapter(projectId, i);
                    regenerated.add(regen);
                }
            } catch (Exception e) {
                log.error("Error regenerando capítulo {} en cascada: {}", i, e.getMessage());
                break; // Detener cascada si hay error
            }
        }
        
        return regenerated;
    }

    @Transactional(readOnly = true)
    public String exportProject(@NonNull UUID projectId) {
        BookProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        StringBuilder sb = new StringBuilder();
        sb.append(project.getTitle()).append("\n\n");
        sb.append(project.getSynopsis()).append("\n\n");
        sb.append("==============================================\n\n");
        project.getChapters().forEach(ch -> {
            if (ch.getContent() != null && !ch.getContent().isBlank()) {
                sb.append(ch.getTitle()).append("\n\n").append(ch.getContent()).append("\n\n");
            }
        });
        return sb.toString();
    }
}
