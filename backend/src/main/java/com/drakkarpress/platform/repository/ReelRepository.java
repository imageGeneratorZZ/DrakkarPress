package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Reel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReelRepository extends JpaRepository<Reel, UUID> {
    List<Reel> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
