package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.ContentHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentHashRepository extends JpaRepository<ContentHash, UUID> {

    Optional<ContentHash> findByHashValueAndIsActiveTrue(String hashValue);

    @Query("SELECT ch FROM ContentHash ch WHERE ch.hashType = :hashType AND ch.isActive = true")
    List<ContentHash> findActiveHashesByType(ContentHash.HashType hashType);

    @Query("SELECT ch FROM ContentHash ch WHERE ch.category = :category AND ch.isActive = true")
    List<ContentHash> findActiveHashesByCategory(ContentHash.ContentCategory category);

    @Query("SELECT COUNT(ch) FROM ContentHash ch WHERE ch.isActive = true")
    Long countActiveHashes();
}
