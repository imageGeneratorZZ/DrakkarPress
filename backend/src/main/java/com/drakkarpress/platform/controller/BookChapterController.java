package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.Chapter;
import com.drakkarpress.platform.security.JwtUserPrincipal;
import com.drakkarpress.platform.service.ChapterService;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books/{bookId}/chapters")
@RequiredArgsConstructor
public class BookChapterController {
    private final ChapterService chapterService;
    private final BookRepository bookRepository;

    @GetMapping
    public ApiResponse<List<Chapter>> list(Authentication auth, @PathVariable UUID bookId) {
        if (!isAuthorized(auth)) return ApiResponse.error("No autenticado");
        return ApiResponse.ok(chapterService.listChapters(bookId));
    }

    @GetMapping("/{number}")
    public ApiResponse<Chapter> get(Authentication auth, @PathVariable UUID bookId, @PathVariable int number) {
        if (!isAuthorized(auth)) return ApiResponse.error("No autenticado");
        return ApiResponse.ok(chapterService.getChapter(bookId, number));
    }

    @PutMapping("/{number}")
    public ApiResponse<Chapter> edit(Authentication auth, @PathVariable UUID bookId, @PathVariable int number, @RequestBody EditRequest body) {
        if (!isAuthorized(auth)) return ApiResponse.error("No autenticado");
        return ApiResponse.ok(chapterService.editChapter(bookId, number, body.content));
    }

    @PostMapping("/{number}/regenerate")
    public ApiResponse<Chapter> regenerate(Authentication auth, @PathVariable UUID bookId, @PathVariable int number, @RequestBody RegenerateRequest body) {
        if (!isAuthorized(auth)) return ApiResponse.error("No autenticado");
        return ApiResponse.ok(chapterService.regenerateChapter(bookId, number, body.promptOverride));
    }

    private boolean isAuthorized(Authentication auth) {
        return auth != null && auth.getPrincipal() instanceof JwtUserPrincipal;
    }

    public static class EditRequest { public String content; }
    public static class RegenerateRequest { public String promptOverride; }
}
