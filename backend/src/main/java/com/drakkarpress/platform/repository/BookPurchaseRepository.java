package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.BookPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookPurchaseRepository extends JpaRepository<BookPurchase, UUID> {

    /**
     * Encuentra todas las compras de un usuario
     */
    List<BookPurchase> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Encuentra compras completadas de un usuario
     */
    List<BookPurchase> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, String status);

    /**
     * Verifica si un usuario ya compró un libro
     */
    boolean existsByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, String status);

    /**
     * Encuentra compra por transacción
     */
    Optional<BookPurchase> findByTransactionId(UUID transactionId);

    /**
     * Encuentra todas las compras de un libro
     */
    List<BookPurchase> findByBookIdOrderByCreatedAtDesc(UUID bookId);

    /**
     * Obtiene compras pendientes de envío de email
     */
    @Query("SELECT bp FROM BookPurchase bp WHERE bp.status = 'COMPLETED' AND bp.emailSent = false")
    List<BookPurchase> findPendingEmailDelivery();

    /**
     * Obtiene compras con links expirados
     */
    List<BookPurchase> findByDownloadExpiresAtBefore(LocalDateTime dateTime);

    /**
     * Cuenta compras por tipo de producto
     */
    @Query("SELECT bp.productType, COUNT(bp) FROM BookPurchase bp WHERE bp.status = 'COMPLETED' GROUP BY bp.productType")
    List<Object[]> countByProductType();

    /**
     * Total de ventas de un libro
     */
    @Query("SELECT COALESCE(SUM(bp.pricePaid), 0) FROM BookPurchase bp WHERE bp.book.id = :bookId AND bp.status = 'COMPLETED'")
    Double getTotalSalesByBook(UUID bookId);

    /**
     * Libros más vendidos
     */
    @Query("SELECT bp.book, COUNT(bp) as sales FROM BookPurchase bp WHERE bp.status = 'COMPLETED' GROUP BY bp.book ORDER BY sales DESC")
    List<Object[]> findBestSellingBooks();
}
