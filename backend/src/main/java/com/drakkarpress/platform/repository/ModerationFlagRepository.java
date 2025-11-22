package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.ModerationFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ModerationFlagRepository extends JpaRepository<ModerationFlag, UUID> {
    List<ModerationFlag> findByStatus(ModerationFlag.Status status);
    List<ModerationFlag> findByResourceId(UUID resourceId);
}
