package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.*;
import com.drakkarpress.platform.repository.*;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final BookCommentRepository bookCommentRepository;
    private final ReelCommentRepository reelCommentRepository;
    private final BookRepository bookRepository;
    private final ReelRepository reelRepository;
    private final PlatformUserRepository userRepository;

    // === Book Comments ===

    @Transactional
    public BookComment createBookComment(UUID bookId, UUID userId, String content, UUID parentCommentId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookComment parent = null;
        if (parentCommentId != null) {
            parent = bookCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        BookComment comment = BookComment.builder()
                .book(book)
                .user(user)
                .content(content)
                .parentComment(parent)
                .likes(0)
                .build();

        BookComment saved = bookCommentRepository.save(comment);
        
        // Increment book comment count
        book.incrementCommentsCount();
        bookRepository.save(book);

        log.info("Created book comment {} for book {} by user {}", saved.getId(), bookId, userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<BookComment> getBookComments(UUID bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookCommentRepository.findByBookAndIsDeletedFalseOrderByCreatedAtDesc(book, pageable);
    }

    @Transactional(readOnly = true)
    public List<BookComment> getBookCommentReplies(UUID parentCommentId) {
        BookComment parent = bookCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return bookCommentRepository.findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(parent);
    }

    @Transactional
    public BookComment updateBookComment(UUID commentId, UUID userId, String newContent) {
        BookComment comment = bookCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: cannot edit other user's comment");
        }

        comment.setContent(newContent);
        comment.markEdited();
        return bookCommentRepository.save(comment);
    }

    @Transactional
    public void deleteBookComment(UUID commentId, UUID userId) {
        BookComment comment = bookCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: cannot delete other user's comment");
        }

        comment.markDeleted();
        bookCommentRepository.save(comment);

        // Decrement book comment count
        Book book = comment.getBook();
        book.decrementCommentsCount();
        bookRepository.save(book);

        log.info("Deleted book comment {} by user {}", commentId, userId);
    }

    // === Reel Comments ===

    @Transactional
    public ReelComment createReelComment(UUID reelId, UUID userId, String content, UUID parentCommentId) {
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReelComment parent = null;
        if (parentCommentId != null) {
            parent = reelCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        ReelComment comment = ReelComment.builder()
                .reel(reel)
                .user(user)
                .content(content)
                .parentComment(parent)
                .likes(0)
                .build();

        ReelComment saved = reelCommentRepository.save(comment);
        
        // Increment reel comment count
        reel.incrementComments();
        reelRepository.save(reel);

        log.info("Created reel comment {} for reel {} by user {}", saved.getId(), reelId, userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<ReelComment> getReelComments(UUID reelId, Pageable pageable) {
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        return reelCommentRepository.findByReelAndIsDeletedFalseOrderByCreatedAtDesc(reel, pageable);
    }

    @Transactional(readOnly = true)
    public List<ReelComment> getReelCommentReplies(UUID parentCommentId) {
        ReelComment parent = reelCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return reelCommentRepository.findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(parent);
    }

    @Transactional
    public ReelComment updateReelComment(UUID commentId, UUID userId, String newContent) {
        ReelComment comment = reelCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: cannot edit other user's comment");
        }

        comment.setContent(newContent);
        comment.markEdited();
        return reelCommentRepository.save(comment);
    }

    @Transactional
    public void deleteReelComment(UUID commentId, UUID userId) {
        ReelComment comment = reelCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: cannot delete other user's comment");
        }

        comment.markDeleted();
        reelCommentRepository.save(comment);

        // Decrement reel comment count
        Reel reel = comment.getReel();
        reel.decrementComments();
        reelRepository.save(reel);

        log.info("Deleted reel comment {} by user {}", commentId, userId);
    }
}
