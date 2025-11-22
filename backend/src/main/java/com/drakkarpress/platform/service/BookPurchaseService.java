package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.BookPurchaseRepository;
import com.drakkarpress.repository.BookRepository;
import com.drakkarpress.platform.repository.PaymentTransactionRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.drakkarpress.platform.repository.RoyaltySplitRepository;
import com.drakkarpress.platform.model.RoyaltySplit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de Compra de Ebooks
 * 
 * Maneja la venta de libros digitales (PDF/EPUB)
 * Integración con Shopify para pagos (Stripe removido)
 * Envío automático por email
 */
@Service
@Slf4j
@SuppressWarnings("null")
public class BookPurchaseService {

    private final BookPurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final PlatformUserRepository userRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${shopify.store.url:https://drakkarpress.myshopify.com}")
    private String shopifyStoreUrl;

    private final RoyaltySplitRepository royaltySplitRepository;

    public BookPurchaseService(
            BookPurchaseRepository purchaseRepository,
            BookRepository bookRepository,
            PlatformUserRepository userRepository,
            PaymentTransactionRepository paymentRepository,
            EmailService emailService,
            RoyaltySplitRepository royaltySplitRepository) {
        this.purchaseRepository = purchaseRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
        this.royaltySplitRepository = royaltySplitRepository;
    }

        /**
         * Crea checkout (Shopify) para comprar ebook.
         * Stripe removido: ahora solo se registra la compra y se devuelve una URL genérica.
         */
        @Transactional
        public Map<String, Object> createEbookCheckout(UUID userId, UUID bookId, String format, String dedicationMessage) {
        // Validar usuario
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar libro
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Verificar si ya lo compró
        if (purchaseRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, "COMPLETED")) {
            throw new RuntimeException("Ya compraste este libro. Descárgalo desde tu biblioteca.");
        }

        BigDecimal price = getPriceByFormat(format);

        // Crear transacción de pago (sin procesar aún, se marcará luego vía webhook manual/admin)
        PaymentTransaction transaction = PaymentTransaction.builder()
            .user(user)
            .paymentProvider("SHOPIFY")
            .amount(price)
            .currency("USD")
            .paymentStatus("PENDING")
            .transactionType("EBOOK_PURCHASE")
            .description("Ebook: " + book.getTitle() + " (" + format.toUpperCase() + ")")
            .build();
        transaction = paymentRepository.save(transaction);

        // Registro de compra inicial (pendiente)
        BookPurchase purchase = BookPurchase.createEbookPurchase(
            user, book, price, format, transaction.getId(), dedicationMessage);
        purchaseRepository.save(purchase);

        // Construir checkout URL (placeholder). En Shopify usar product/variant real y cart.
        String checkoutUrl = shopifyStoreUrl + "/cart/add?id=SHOPIFY_EBOOK_VARIANT_ID&quantity=1";

        Map<String, Object> response = new HashMap<>();
        response.put("checkoutUrl", checkoutUrl);
        response.put("purchaseId", purchase.getId());
        response.put("transactionId", transaction.getId());
        response.put("provider", "SHOPIFY");
        return response;
        }

    /**
     * Webhook (Shopify) o confirmación manual: marcar compra completada.
     * Este método debería llamarse cuando se confirma el pago en Shopify.
     */
    @Transactional
    public void markEbookPurchaseCompleted(UUID purchaseId) {
        BookPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada: " + purchaseId));

        if (purchase.isCompleted()) {
            return; // Ya procesada
        }

        purchase.markCompleted();
        purchase.generateDownloadLink(frontendUrl, 72); // 72 horas de validez
        purchaseRepository.save(purchase);

        PaymentTransaction transaction = paymentRepository.findById(purchase.getTransactionId())
                .orElse(null);
        if (transaction != null && !"COMPLETED".equals(transaction.getPaymentStatus())) {
            transaction.markCompleted("SHOPIFY_ORDER_ID_PLACEHOLDER");
            paymentRepository.save(transaction);
        }

        emailService.sendEbookPurchaseConfirmation(purchase);
        log.info("Compra completada (Shopify) ebook: {} usuario: {}", purchase.getBook().getTitle(), purchase.getUser().getEmail());

        // Calcular y registrar comisión / royalty split interno
        try {
            calculateAndPersistInternalRoyalty(purchase);
        } catch (Exception ex) {
            log.error("Error calculando royalty split para compra {}: {}", purchaseId, ex.getMessage());
        }
    }

    /**
     * Obtiene biblioteca de ebooks del usuario
     */
    public List<BookPurchase> getUserLibrary(UUID userId) {
        return purchaseRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "COMPLETED");
    }

    /**
     * Regenera link de descarga
     */
    @Transactional
    public String regenerateDownloadLink(UUID purchaseId, UUID userId) {
        BookPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        // Verificar que pertenece al usuario
        if (!purchase.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para acceder a esta compra");
        }

        // Verificar que puede descargar
        if (!purchase.canDownload()) {
            throw new RuntimeException("Has alcanzado el límite de descargas para este ebook");
        }

        // Generar nuevo link
        purchase.generateDownloadLink(frontendUrl, 72);
        purchaseRepository.save(purchase);

        return purchase.getDownloadLink();
    }

    /**
     * Registra descarga
     */
    @Transactional
    public void recordDownload(UUID purchaseId) {
        BookPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        purchase.incrementDownloadCount();
        purchaseRepository.save(purchase);

        log.info("Descarga #{} registrada - Compra: {}", purchase.getDownloadCount(), purchaseId);
    }

    /**
     * Procesa compras pendientes de envío de email
     */
    @Transactional
    public void processPendingEmailDeliveries() {
        List<BookPurchase> pendingPurchases = purchaseRepository.findPendingEmailDelivery();
        
        log.info("Procesando {} compras pendientes de envío", pendingPurchases.size());
        
        for (BookPurchase purchase : pendingPurchases) {
            try {
                emailService.sendEbookPurchaseConfirmation(purchase);
                purchase.markEmailSent();
                purchaseRepository.save(purchase);
                log.info("Email enviado para compra: {}", purchase.getId());
            } catch (Exception e) {
                log.error("Error enviando email para compra {}: {}", purchase.getId(), e.getMessage());
            }
        }
    }

    // ========================================================================
    // UTILIDADES
    // ========================================================================

    /**
     * Obtiene precio según formato
     */
    private BigDecimal getPriceByFormat(String format) {
        return switch (format.toUpperCase()) {
            case "PDF" -> new BigDecimal("9.99");
            case "EPUB" -> new BigDecimal("9.99");
            case "MOBI" -> new BigDecimal("9.99");
            case "BUNDLE" -> new BigDecimal("14.99"); // PDF + EPUB + MOBI
            default -> new BigDecimal("9.99");
        };
    }

    /**
     * Verifica si usuario compró un libro
     */
    public boolean hasUserPurchased(UUID userId, UUID bookId) {
        return purchaseRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, "COMPLETED");
    }

    /**
     * Obtiene estadísticas de ventas de un libro
     */
    public Map<String, Object> getBookSalesStats(UUID bookId) {
        List<BookPurchase> purchases = purchaseRepository.findByBookIdOrderByCreatedAtDesc(bookId);
        
        long totalSales = purchases.stream()
                .filter(BookPurchase::isCompleted)
                .count();
        
        double totalRevenue = purchases.stream()
                .filter(BookPurchase::isCompleted)
                .mapToDouble(p -> p.getPricePaid().doubleValue())
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales);
        stats.put("totalRevenue", totalRevenue);
        stats.put("averagePrice", totalSales > 0 ? totalRevenue / totalSales : 0);
        
        return stats;
    }

    // ========================================================================
    // COMISIONES / ROYALTIES INTERNOS
    // ========================================================================

    @Transactional
    protected void calculateAndPersistInternalRoyalty(BookPurchase purchase) {
        if (!purchase.isCompleted()) return;

        var user = purchase.getUser();
        var book = purchase.getBook();
        var gross = purchase.getPricePaid();
        if (gross == null) return;

        // Comisión plataforma (25% si usuario FREE, 5% si PREMIUM)
        boolean isFree = user.getSubscription() != null && user.getSubscription().equalsIgnoreCase("FREE");
        var platformFee = isFree 
            ? gross.multiply(new java.math.BigDecimal("0.25"))  // 25% FREE
            : gross.multiply(new java.math.BigDecimal("0.05")); // 5% PREMIUM
        var net = gross.subtract(platformFee);

        RoyaltySplit split = RoyaltySplit.builder()
                .bookId(book.getId())
                .userId(user.getId())
                .percentage(new java.math.BigDecimal("100.00"))
                .grossAmount(gross)
                .platformFee(platformFee)
                .netAmount(net)
                .source(RoyaltySplit.Source.INTERNAL_SALE)
                .build();
        royaltySplitRepository.save(split);
    }
}
