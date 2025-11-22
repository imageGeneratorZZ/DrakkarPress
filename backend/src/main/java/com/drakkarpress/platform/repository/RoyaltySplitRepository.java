package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.RoyaltySplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoyaltySplitRepository extends JpaRepository<RoyaltySplit, UUID> {
    List<RoyaltySplit> findByBookId(UUID bookId);
    List<RoyaltySplit> findByUserId(UUID userId);
}
