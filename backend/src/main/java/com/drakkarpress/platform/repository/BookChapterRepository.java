package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.BookChapter;
import com.drakkarpress.platform.model.BookProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookChapterRepository extends JpaRepository<BookChapter, UUID> {
    List<BookChapter> findByProjectOrderByChapterOrderAsc(BookProject project);
    Optional<BookChapter> findByProjectAndChapterOrder(BookProject project, Integer chapterOrder);
}
