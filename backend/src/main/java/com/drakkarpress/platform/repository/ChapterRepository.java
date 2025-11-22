package com.drakkarpress.platform.repository;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByBookOrderByChapterNumberAsc(Book book);
    Optional<Chapter> findByBookAndChapterNumber(Book book, int chapterNumber);
    long countByBook(Book book);
}
