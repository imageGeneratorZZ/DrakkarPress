package com.drakkarpress.platform.service;

import com.drakkarpress.model.Book;
import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.BookPurchaseRepository;
import com.drakkarpress.repository.BookRepository;
import com.drakkarpress.platform.repository.PaymentTransactionRepository;
import com.drakkarpress.platform.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de Compra de Ebooks
 * 
 * Maneja la venta de libros digitales (PDF/EPUB)
 * Integración con Stripe para pagos
 * Envío automático por email
 */
@Service
@Slf4j
public class BookPurchaseService {

    private final BookPurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final EmailService emailService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public BookPurchaseService(
            BookPurchaseRepository purchaseRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            PaymentTransactionRepository paymentRepository,
            EmailService emailService) {
        this.purchaseRepository = purchaseRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }

    /**
     * Crea sesión de checkout para comprar ebook
     */
    @Transactional
    public Map<String, Object> createEbookCheckout(UUID userId, UUID bookId, String format, String dedicationMessage) {
        try {
            Stripe.apiKey = stripeApiKey;

            // Validar usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar libro
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

            // Verificar si ya lo compró
            boolean alreadyPurchased = purchaseRepository.existsByUserIdAndBookIdAndStatus(
                    userId, bookId, "COMPLETED");
            
            if (alreadyPurchased) {
                throw new RuntimeException("Ya compraste este libro. Descárgalo desde tu biblioteca.");
            }

            // Determinar precio según formato
            BigDecimal price = getPriceByFormat(format);
            long priceInCents = price.multiply(new BigDecimal(100)).longValue();

            // Crear transacción de pago
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .user(user)
                    .paymentProvider("STRIPE")
                    .amount(price)
                    .currency("USD")
                    .paymentStatus("PENDING")
                    .transactionType("EBOOK_PURCHASE")
                    .description("Ebook: " + book.getTitle() + " (" + format.toUpperCase() + ")")
                    .build();
            transaction = paymentRepository.save(transaction);

            // Crear registro de compra
                BookPurchase purchase = BookPurchase.createEbookPurchase(
                    user, book, price, format, transaction.getId(), dedicationMessage);
            purchase = purchaseRepository.save(purchase);

            // URLs de retorno
            String successUrl = frontendUrl + "/purchase-success.html?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = frontendUrl + "/shop.html?cancelled=true";

            // Crear sesión de Stripe
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(user.getEmail())
                    .setClientReferenceId(purchase.getId().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(priceInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(book.getTitle())
                                                                    .setDescription("Ebook " + format.toUpperCase() + " - Descarga inmediata")
                                                                    .addImage(book.getCoverImageUrl() != null ? book.getCoverImageUrl() : "")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("user_id", userId.toString())
                    .putMetadata("book_id", bookId.toString())
                    .putMetadata("purchase_id", purchase.getId().toString())
                    .putMetadata("format", format)
                    .build();

            Session session = Session.create(params);

            // Actualizar transacción
            transaction.setExternalTransactionId(session.getId());
            paymentRepository.save(transaction);

            log.info("Checkout creado para ebook: {} - Usuario: {}", book.getTitle(), user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());
            response.put("purchaseId", purchase.getId());

            return response;

        } catch (StripeException e) {
            log.error("Error creando checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    /**
     * Maneja webhook de pago completado para ebook
     */
    @Transactional
    public void handleEbookPurchaseCompleted(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        
        if (!deserializer.getObject().isPresent()) {
            log.error("No se pudo deserializar evento");
            return;
        }

        Session session = (Session) deserializer.getObject().get();
        String purchaseIdStr = session.getClientReferenceId();
        
        if (purchaseIdStr == null) {
            log.error("No purchase_id en session");
            return;
        }

        UUID purchaseId = UUID.fromString(purchaseIdStr);
        BookPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada: " + purchaseId));

        // Marcar como completada
        purchase.markCompleted();
        
        // Generar link de descarga
        purchase.generateDownloadLink(frontendUrl, 72); // 72 horas de validez
        
        purchaseRepository.save(purchase);

        // Actualizar transacción de pago
        PaymentTransaction transaction = paymentRepository.findById(purchase.getTransactionId())
                .orElse(null);
        if (transaction != null) {
            transaction.markCompleted(session.getId());
            paymentRepository.save(transaction);
        }

        log.info("Compra de ebook completada: {} - Usuario: {}", 
                 purchase.getBook().getTitle(), purchase.getUser().getEmail());

        // Enviar email con link de descarga
        emailService.sendEbookPurchaseConfirmation(purchase);
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
}
