package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.PublicationJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicationJobRepository extends JpaRepository<PublicationJob, UUID> {
    Page<PublicationJob> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    List<PublicationJob> findByStatus(PublicationJob.PublicationStatus status);
    Optional<PublicationJob> findByBookId(UUID bookId);
    List<PublicationJob> findByBookIdOrderByCreatedAtDesc(UUID bookId);
    long countByStatus(PublicationJob.PublicationStatus status);
}
