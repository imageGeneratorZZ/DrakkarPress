package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID> {
    List<Story> findByUserIdAndExpiresAtAfter(UUID userId, LocalDateTime now);
}
