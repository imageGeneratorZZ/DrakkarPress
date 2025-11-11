package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    /**
     * Buscar transacciones de usuario
     */
    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Buscar por ID externo
     */
    Optional<PaymentTransaction> findByExternalTransactionId(String externalTransactionId);

    /**
     * Buscar por estado
     */
    List<PaymentTransaction> findByPaymentStatusOrderByCreatedAtDesc(String status);

    /**
     * Buscar transacciones completadas de usuario
     */
    List<PaymentTransaction> findByUserIdAndPaymentStatusOrderByCompletedAtDesc(UUID userId, String status);

    /**
     * Buscar transacciones pendientes
     */
    List<PaymentTransaction> findByPaymentStatusAndCreatedAtBefore(String status, LocalDateTime before);

    /**
     * Calcular revenue total
     */
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED'")
    BigDecimal calculateTotalRevenue();

    /**
     * Calcular revenue por período
     */
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED' AND pt.completedAt BETWEEN :start AND :end")
    BigDecimal calculateRevenueInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Transacciones por proveedor
     */
    List<PaymentTransaction> findByPaymentProviderOrderByCreatedAtDesc(String provider);

    /**
     * Estadísticas por plan
     */
    @Query("SELECT pt.planType, COUNT(pt), SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.paymentStatus = 'COMPLETED' GROUP BY pt.planType")
    List<Object[]> getRevenueByPlan();

    /**
     * Transacciones recientes
     */
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.createdAt >= :since ORDER BY pt.createdAt DESC")
    List<PaymentTransaction> findRecentTransactions(@Param("since") LocalDateTime since);

    /**
     * Buscar reembolsos
     */
    List<PaymentTransaction> findByPaymentStatusOrderByRefundedAtDesc(String status);
}
