package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.ExportJob;
import com.drakkarpress.platform.repository.ExportJobRepository;
import com.drakkarpress.repository.BookRepository;
import com.drakkarpress.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
public class ExportJobServiceTest {

    private ExportJobRepository exportJobRepository;
    private BookRepository bookRepository;
    private ExportJobService service;

    @BeforeEach
    void setup() {
        exportJobRepository = mock(ExportJobRepository.class);
        bookRepository = mock(BookRepository.class);
        service = new ExportJobService(exportJobRepository, bookRepository);
    }

    @Test
    void testSuccessfulJobFlow() {
        UUID bookId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).title("Test").pages(100).priceDigital(null).genre(Book.Genre.FANTASY).build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        ExportJob job = ExportJob.builder()
                .id(UUID.randomUUID())
                .bookId(bookId)
                .platform(ExportJob.Platform.KDP)
                .status(ExportJob.Status.PENDING)
                .attempts(0)
                .build();

        when(exportJobRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exportJobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        // Crear y procesar sync
        when(exportJobRepository.save(job)).thenReturn(job);
        exportJobRepository.save(job);
        service.processJobSync(job.getId());
        assertEquals(ExportJob.Status.COMPLETED, job.getStatus());
        assertTrue(job.getAttempts() >= 1);
    }
}
