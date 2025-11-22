package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.BookGenerationJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookGenerationJobRepository extends JpaRepository<BookGenerationJob, UUID> {
    Page<BookGenerationJob> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    List<BookGenerationJob> findByStatus(BookGenerationJob.JobStatus status);
    @Query("SELECT j FROM BookGenerationJob j WHERE j.user.id = ?1 AND j.status = ?2 ORDER BY j.createdAt DESC")
    List<BookGenerationJob> findByUserIdAndStatus(UUID userId, BookGenerationJob.JobStatus status);
    Optional<BookGenerationJob> findByBookId(UUID bookId);
    long countByUserIdAndStatus(UUID userId, BookGenerationJob.JobStatus status);
    long countByStatus(BookGenerationJob.JobStatus status);
}
