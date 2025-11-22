package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.ExportJob;
import com.drakkarpress.platform.repository.ExportJobRepository;
import com.drakkarpress.platform.service.ExportJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/export/jobs")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ExportJobController {

    private final ExportJobService exportJobService;
    private final ExportJobRepository exportJobRepository;

    @PostMapping
    public ApiResponse<ExportJob> createJob(@RequestBody Map<String, String> body) {
        UUID bookId = UUID.fromString(body.get("bookId"));
        ExportJob.Platform platform = ExportJob.Platform.valueOf(body.get("platform"));
        ExportJob job = exportJobService.createJob(bookId, platform);
        return ApiResponse.ok("Job creado", job);
    }

    @GetMapping
    public ApiResponse<List<ExportJob>> listJobs() {
        return ApiResponse.ok(exportJobRepository.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ExportJob> getJob(@PathVariable UUID id) {
        ExportJob job = exportJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job no encontrado"));
        return ApiResponse.ok(job);
    }

    @PostMapping("/{id}/retry")
    public ApiResponse<ExportJob> retry(@PathVariable UUID id) {
        ExportJob job = exportJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job no encontrado"));
        job.setStatus(ExportJob.Status.PENDING);
        job.setLastError(null);
        exportJobRepository.save(job);
        exportJobService.processJobAsync(job.getId());
        return ApiResponse.ok("Reintentando", job);
    }
}
