package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.RoyaltySplit;
import com.drakkarpress.platform.repository.RoyaltySplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/royalties")
@RequiredArgsConstructor
public class RoyaltyController {

    private final RoyaltySplitRepository royaltySplitRepository;

    @GetMapping
    public ApiResponse<List<RoyaltySplit>> listByUser(@RequestParam UUID userId) {
        return ApiResponse.ok(royaltySplitRepository.findByUserId(userId));
    }

    @GetMapping("/{bookId}")
    public ApiResponse<List<RoyaltySplit>> listByBook(@PathVariable UUID bookId) {
        return ApiResponse.ok(royaltySplitRepository.findByBookId(bookId));
    }
}
