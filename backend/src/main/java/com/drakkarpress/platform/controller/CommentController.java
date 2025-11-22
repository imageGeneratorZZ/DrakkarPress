package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.BookComment;
import com.drakkarpress.platform.model.ReelComment;
import com.drakkarpress.platform.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // === Book Comments ===

    @PostMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<BookComment>> createBookComment(
            @PathVariable UUID bookId,
            @RequestParam UUID userId, // TODO: Replace with JWT principal
            @RequestBody Map<String, String> request) {
        
        String content = request.get("content");
        String parentIdStr = request.get("parentCommentId");
        UUID parentId = parentIdStr != null ? UUID.fromString(parentIdStr) : null;

        BookComment comment = commentService.createBookComment(bookId, userId, content, parentId);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<Page<BookComment>>> getBookComments(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookComment> comments = commentService.getBookComments(bookId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/books/comment/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<BookComment>>> getBookCommentReplies(
            @PathVariable UUID commentId) {
        
        List<BookComment> replies = commentService.getBookCommentReplies(commentId);
        return ResponseEntity.ok(ApiResponse.success(replies));
    }

    @PutMapping("/books/comment/{commentId}")
    public ResponseEntity<ApiResponse<BookComment>> updateBookComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId, // TODO: Replace with JWT principal
            @RequestBody Map<String, String> request) {
        
        String newContent = request.get("content");
        BookComment updated = commentService.updateBookComment(commentId, userId, newContent);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/books/comment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteBookComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) { // TODO: Replace with JWT principal
        
        commentService.deleteBookComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // === Reel Comments ===

    @PostMapping("/reels/{reelId}")
    public ResponseEntity<ApiResponse<ReelComment>> createReelComment(
            @PathVariable UUID reelId,
            @RequestParam UUID userId, // TODO: Replace with JWT principal
            @RequestBody Map<String, String> request) {
        
        String content = request.get("content");
        String parentIdStr = request.get("parentCommentId");
        UUID parentId = parentIdStr != null ? UUID.fromString(parentIdStr) : null;

        ReelComment comment = commentService.createReelComment(reelId, userId, content, parentId);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @GetMapping("/reels/{reelId}")
    public ResponseEntity<ApiResponse<Page<ReelComment>>> getReelComments(
            @PathVariable UUID reelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReelComment> comments = commentService.getReelComments(reelId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/reels/comment/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<ReelComment>>> getReelCommentReplies(
            @PathVariable UUID commentId) {
        
        List<ReelComment> replies = commentService.getReelCommentReplies(commentId);
        return ResponseEntity.ok(ApiResponse.success(replies));
    }

    @PutMapping("/reels/comment/{commentId}")
    public ResponseEntity<ApiResponse<ReelComment>> updateReelComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId, // TODO: Replace with JWT principal
            @RequestBody Map<String, String> request) {
        
        String newContent = request.get("content");
        ReelComment updated = commentService.updateReelComment(commentId, userId, newContent);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/reels/comment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteReelComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) { // TODO: Replace with JWT principal
        
        commentService.deleteReelComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
