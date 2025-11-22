package com.drakkarpress.platform.repository;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.BookComment;
import com.drakkarpress.platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, UUID> {

    Page<BookComment> findByBookAndIsDeletedFalseOrderByCreatedAtDesc(Book book, Pageable pageable);

    List<BookComment> findByBookAndIsDeletedFalseOrderByCreatedAtDesc(Book book);

    @Query("SELECT COUNT(c) FROM BookComment c WHERE c.book = :book AND c.isDeleted = false")
    Long countByBookAndNotDeleted(Book book);

    List<BookComment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(BookComment parentComment);

    List<BookComment> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);
}
