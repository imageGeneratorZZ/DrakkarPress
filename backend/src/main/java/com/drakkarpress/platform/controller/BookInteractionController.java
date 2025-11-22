package com.drakkarpress.platform.controller;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookInteractionController {

    private final BookRepository bookRepository;

    @PostMapping("/{id}/like")
    public ApiResponse<Book> like(@PathVariable UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        book.incrementLikes();
        bookRepository.save(book);
        return ApiResponse.ok("Like registrado", book);
    }

    @PostMapping("/{id}/unlike")
    public ApiResponse<Book> unlike(@PathVariable UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        book.decrementLikes();
        bookRepository.save(book);
        return ApiResponse.ok("Like removido", book);
    }

    @PostMapping("/{id}/comment")
    public ApiResponse<Map<String, Object>> comment(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        // TODO: Persistir entidad Comment real; por ahora sólo contador
        book.incrementCommentsCount();
        bookRepository.save(book);
        return ApiResponse.ok(Map.of(
                "bookId", book.getId(),
                "commentsCount", book.getCommentsCount(),
                "comment", body.getOrDefault("text", "")
        ));
    }
}
