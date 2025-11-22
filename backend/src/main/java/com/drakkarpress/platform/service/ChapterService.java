package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.Chapter;
import com.drakkarpress.platform.model.BookGenerationJob;
import com.drakkarpress.platform.repository.BookGenerationJobRepository;
import com.drakkarpress.platform.repository.ChapterRepository;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final BookGenerationJobRepository jobRepository;
    private final AiBookGenerationService aiBookGenerationService;

    public List<Chapter> listChapters(UUID bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        return chapterRepository.findByBookOrderByChapterNumberAsc(book);
    }

    public Chapter getChapter(UUID bookId, int number) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        return chapterRepository.findByBookAndChapterNumber(book, number).orElseThrow();
    }

    @Transactional
    public Chapter editChapter(UUID bookId, int number, String newContent) {
        Chapter ch = getChapter(bookId, number);
        ch.setEditedContent(newContent);
        ch.setRegenerated(false);
        return chapterRepository.save(ch);
    }

    @Transactional
    public Chapter regenerateChapter(UUID bookId, int number, String promptOverride) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        Chapter ch = chapterRepository.findByBookAndChapterNumber(book, number).orElseThrow();
        BookGenerationJob job = jobRepository.findByBookId(bookId).orElseThrow();
        // Reutilizar lógica de generación de capítulo
        String regenerated = aiBookGenerationService.regenerateSingleChapter(job, number, promptOverride);
        ch.setOriginalContent(regenerated);
        ch.setEditedContent(null);
        ch.setRegenerated(true);
        ch.setAiModel(job.getAiModel());
        return chapterRepository.save(ch);
    }
}
