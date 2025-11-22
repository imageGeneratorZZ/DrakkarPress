package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.ExportJob;
import com.drakkarpress.platform.repository.ExportJobRepository;
import com.drakkarpress.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class ExportJobService {

    private final ExportJobRepository exportJobRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ExportJob createJob(UUID bookId, ExportJob.Platform platform) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        ExportJob job = ExportJob.builder()
                .bookId(book.getId())
                .platform(platform)
                .status(ExportJob.Status.PENDING)
                .attempts(0)
                .build();
        exportJobRepository.save(job);
        processJobAsync(job.getId());
        return job;
    }

    @Async
    public void processJobAsync(UUID jobId) {
        attemptProcess(jobId);
    }

    public void processJobSync(UUID jobId) { attemptProcess(jobId); }

    private void attemptProcess(UUID jobId) {
        int maxAttempts = 3;
        ExportJob job = exportJobRepository.findById(jobId).orElse(null);
        if (job == null) return;
        while (job.getAttempts() < maxAttempts && job.getStatus() != ExportJob.Status.COMPLETED) {
            try {
                job.setAttempts(job.getAttempts() + 1);
                exportJobRepository.save(job);
                runStages(job);
                return; // success
            } catch (TransientStageException tse) {
                log.warn("Transient error en intento {} job {}: {}", job.getAttempts(), job.getId(), tse.getMessage());
                job.setLastError(tse.getMessage());
                if (job.getAttempts() >= maxAttempts) {
                    job.setStatus(ExportJob.Status.FAILED);
                    exportJobRepository.save(job);
                } else {
                    sleepBackoff(job.getAttempts());
                }
            } catch (Exception e) {
                log.error("Error fatal job {}: {}", job.getId(), e.getMessage());
                job.setLastError(e.getMessage());
                job.setStatus(ExportJob.Status.FAILED);
                exportJobRepository.save(job);
                return;
            }
        }
    }

    private void runStages(ExportJob job) {
        updateStatus(job, ExportJob.Status.BUILDING);
        // stub generar archivos
        simulateTransient(0.1);
        updateStatus(job, ExportJob.Status.CONVERTING);
        simulateTransient(0.1);
        updateStatus(job, ExportJob.Status.UPLOADING);
        simulateTransient(0.2);
        updateStatus(job, ExportJob.Status.VERIFYING);
        simulateTransient(0.05);
        updateStatus(job, ExportJob.Status.COMPLETED);
    }

    private void sleepBackoff(int attempt) {
        try { Thread.sleep(500L * attempt); } catch (InterruptedException ignored) {}
    }

    private void simulateTransient(double probability) {
        if (Math.random() < probability) {
            throw new TransientStageException("Error transitorio simulado");
        }
    }

    static class TransientStageException extends RuntimeException {
        TransientStageException(String msg) { super(msg); }
    }

    @Transactional
    protected void updateStatus(ExportJob job, ExportJob.Status status) {
        job.setStatus(status);
        exportJobRepository.save(job);
    }

    public List<ExportJob> listPending() {
        return exportJobRepository.findByStatus(ExportJob.Status.PENDING);
    }
}
