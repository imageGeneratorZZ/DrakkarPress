package com.drakkarpress.platform.controller;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.dto.response.ApiResponse;
import com.drakkarpress.platform.dto.BookPublicResponse;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/books")
@RequiredArgsConstructor
public class PublicBooksController {

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookPublicResponse>>> listPublished() {
        List<Book> books = bookRepository.findPublishedBooksOrderByDate();
        List<BookPublicResponse> data = books.stream().map(BookPublicResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Published books", data));
    }
}
