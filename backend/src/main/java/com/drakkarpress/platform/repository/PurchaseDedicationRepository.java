package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.PurchaseDedication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseDedicationRepository extends JpaRepository<PurchaseDedication, UUID> {
    Optional<PurchaseDedication> findByHash(String hash);
}
