package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.CommissionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommissionConfigRepository extends JpaRepository<CommissionConfig, UUID> {
    List<CommissionConfig> findByContextAndIsActiveTrueOrderByMinVolumeAsc(String context);
    Optional<CommissionConfig> findFirstByContextAndIsActiveTrueOrderByMinVolumeAsc(String context);
}
