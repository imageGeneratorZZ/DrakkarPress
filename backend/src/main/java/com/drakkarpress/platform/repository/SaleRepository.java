package com.drakkarpress.platform.repository;

import com.drakkarpress.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    @Query("SELECT COALESCE(SUM(s.amount),0) FROM Sale s WHERE s.book.author.id = :authorId AND s.paymentStatus = 'COMPLETED'")
    BigDecimal totalRevenueForAuthor(@Param("authorId") UUID authorId);

    @Query("SELECT COALESCE(SUM(s.amount),0) FROM Sale s WHERE s.reseller.id = :resellerId AND s.paymentStatus = 'COMPLETED'")
    BigDecimal totalRevenueForReseller(@Param("resellerId") UUID resellerId);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate >= :since")
    long countSalesSince(@Param("since") LocalDateTime since);
}
