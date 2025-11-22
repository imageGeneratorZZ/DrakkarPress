package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Reel;
import com.drakkarpress.platform.model.ReelComment;
import com.drakkarpress.platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReelCommentRepository extends JpaRepository<ReelComment, UUID> {

    Page<ReelComment> findByReelAndIsDeletedFalseOrderByCreatedAtDesc(Reel reel, Pageable pageable);

    List<ReelComment> findByReelAndIsDeletedFalseOrderByCreatedAtDesc(Reel reel);

    @Query("SELECT COUNT(c) FROM ReelComment c WHERE c.reel = :reel AND c.isDeleted = false")
    Long countByReelAndNotDeleted(Reel reel);

    List<ReelComment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(ReelComment parentComment);

    List<ReelComment> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);
}
