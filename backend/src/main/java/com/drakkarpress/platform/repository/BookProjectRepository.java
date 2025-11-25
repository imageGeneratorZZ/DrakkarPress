package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.BookProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookProjectRepository extends JpaRepository<BookProject, UUID> {
    List<BookProject> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
}
