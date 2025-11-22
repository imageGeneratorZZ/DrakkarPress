package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExportJobRepository extends JpaRepository<ExportJob, UUID> {
    List<ExportJob> findByStatus(ExportJob.Status status);
}
