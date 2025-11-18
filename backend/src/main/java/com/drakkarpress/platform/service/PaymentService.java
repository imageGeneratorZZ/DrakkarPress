package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.MembershipRepository;
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
 * Servicio de Pagos con Stripe
 * 
 * Características:
 * - Integración con Stripe Checkout
 * - Manejo de webhooks
 * - Gestión de transacciones
 * - Activación automática de membresías
 */
@Service
@Slf4j
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final EmailService emailService;
    private final PricingService pricingService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PaymentService(
            PaymentTransactionRepository paymentRepository,
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            EmailService emailService,
            PricingService pricingService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.emailService = emailService;
        this.pricingService = pricingService;
    }

    /**
     * Crea sesión de Stripe Checkout
     */
    @Transactional
    public Map<String, Object> createCheckoutSession(UUID userId, String planType, String frequency) {
        try {
            // Inicializar Stripe
            Stripe.apiKey = stripeApiKey;

            // Obtener usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener precio
            PricingService.PricingInfo pricing = pricingService.getCurrentPricing(planType, frequency);
            long amountInCents = pricing.getPriceInCents();

            // Crear transacción pendiente
            PaymentTransaction transaction = PaymentTransaction.createSignup(
                    user,
                    "STRIPE",
                    new BigDecimal(amountInCents).divide(new BigDecimal(100)),
                    planType,
                    frequency
            );
            transaction = paymentRepository.save(transaction);

            // URLs de retorno
            String successUrl = frontendUrl + "/checkout-success.html?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = frontendUrl + "/premium.html?cancelled=true";

            // Crear sesión de Stripe
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(user.getEmail())
                    .setClientReferenceId(transaction.getId().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("DrakkarPress " + getPlanDisplayName(planType))
                                                                    .setDescription(getFrequencyDisplayName(frequency) + " - Acceso completo")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("user_id", userId.toString())
                    .putMetadata("transaction_id", transaction.getId().toString())
                    .putMetadata("plan_type", planType)
                    .putMetadata("frequency", frequency)
                    .build();

            Session session = Session.create(params);

            // Actualizar transacción con session ID
            transaction.setExternalTransactionId(session.getId());
            paymentRepository.save(transaction);

            log.info("Sesión de checkout creada: {} para usuario: {}", session.getId(), user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());
            response.put("transactionId", transaction.getId());

            return response;

        } catch (StripeException e) {
            log.error("Error al crear sesión de Stripe: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    /**
     * Maneja webhooks de Stripe
     */
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        try {
            Event event = Event.constructFrom(payload);

            // Verificar firma (en producción)
            if (webhookSecret != null && !webhookSecret.isEmpty()) {
                event = com.stripe.net.Webhook.constructEvent(payload, sigHeader, webhookSecret);
            }

            log.info("Webhook recibido: {}", event.getType());

            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutCompleted(event);
                    break;
                case "checkout.session.async_payment_succeeded":
                    handleCheckoutCompleted(event);
                    break;
                case "checkout.session.async_payment_failed":
                    handleCheckoutFailed(event);
                    break;
                default:
                    log.info("Evento no manejado: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Error procesando webhook");
        }
    }

    /**
     * Maneja pago completado
     */
    private void handleCheckoutCompleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        Session session = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            session = (Session) dataObjectDeserializer.getObject().get();
        } else {
            log.error("No se pudo deserializar el objeto del evento");
            return;
        }

        String transactionIdStr = session.getClientReferenceId();
        if (transactionIdStr == null) {
            log.error("No se encontró transaction_id en session");
            return;
        }

        UUID transactionId = UUID.fromString(transactionIdStr);
        PaymentTransaction transaction = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada: " + transactionId));

        // Marcar como completada
        transaction.markCompleted(session.getId());
        transaction.setPaymentMethod(session.getPaymentMethodTypes().get(0));
        paymentRepository.save(transaction);

        // Activar membresía
        User user = transaction.getUser();
        activateMembership(user, transaction);

        log.info("Pago completado para usuario: {} - Transaction: {}", user.getEmail(), transactionId);

        // Enviar email de confirmación
        PricingService.PricingInfo pricing = pricingService.getCurrentPricing(
                transaction.getPlanType(),
                transaction.getPaymentFrequency()
        );
        emailService.sendPurchaseConfirmation(user, transaction, pricing);
    }

    /**
     * Maneja pago fallido
     */
    private void handleCheckoutFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        Session session = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            session = (Session) dataObjectDeserializer.getObject().get();
        } else {
            return;
        }

        String transactionIdStr = session.getClientReferenceId();
        if (transactionIdStr == null) return;

        UUID transactionId = UUID.fromString(transactionIdStr);
        PaymentTransaction transaction = paymentRepository.findById(transactionId)
                .orElse(null);

        if (transaction != null) {
            transaction.markFailed("Pago asíncrono falló");
            paymentRepository.save(transaction);
            log.warn("Pago fallido para transacción: {}", transactionId);
        }
    }

    /**
     * Activa membresía después de pago exitoso
     */
    private void activateMembership(User user, PaymentTransaction transaction) {
        String planType = transaction.getPlanType();
        String frequency = transaction.getPaymentFrequency();

        // Calcular fecha de expiración
        LocalDateTime expiresAt = calculateExpirationDate(frequency);

        // Buscar membresía existente o crear nueva
        List<Membership> memberships = membershipRepository.findByUserId(user.getId());
        Membership membership;

        if (!memberships.isEmpty()) {
            membership = memberships.get(0);
            membership.setPlanType(planType);
            membership.setStatus("ACTIVE");
            membership.setExpiresAt(expiresAt);
            membership.setPaymentFrequency(frequency);
        } else {
            membership = Membership.builder()
                    .user(user)
                    .planType(planType)
                    .status("ACTIVE")
                    .expiresAt(expiresAt)
                    .paymentFrequency(frequency)
                    .build();
        }

        membershipRepository.save(membership);

        // Actualizar transaction con membership_id
        transaction.setMembershipId(membership.getId());
        paymentRepository.save(transaction);

        log.info("Membresía activada: {} para usuario: {}", planType, user.getEmail());
    }

    /**
     * Calcula fecha de expiración
     */
    private LocalDateTime calculateExpirationDate(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        return switch (frequency) {
            case "MONTHLY" -> now.plusMonths(1);
            case "ANNUAL" -> now.plusYears(1);
            case "LIFETIME" -> now.plusYears(100); // Lifetime
            default -> now.plusMonths(1);
        };
    }

    /**
     * Obtiene historial de pagos
     */
    public List<PaymentTransaction> getPaymentHistory(UUID userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Verifica estado de sesión
     */
    public Map<String, Object> getSessionStatus(String sessionId) {
        try {
            Stripe.apiKey = stripeApiKey;
            Session session = Session.retrieve(sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", session.getPaymentStatus());
            response.put("customerEmail", session.getCustomerEmail());

            return response;

        } catch (StripeException e) {
            log.error("Error al obtener sesión: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar estado de pago");
        }
    }

    // ========================================================================
    // UTILIDADES
    // ========================================================================

    private String getPlanDisplayName(String planType) {
        return switch (planType) {
            case "PREMIUM_PHASE_1" -> "Fundador";
            case "PREMIUM_PHASE_2" -> "Early Adopter";
            case "PREMIUM_PHASE_3" -> "Premium";
            default -> planType;
        };
    }

    private String getFrequencyDisplayName(String frequency) {
        return switch (frequency) {
            case "MONTHLY" -> "Mensual";
            case "ANNUAL" -> "Anual";
            case "LIFETIME" -> "Lifetime";
            default -> frequency;
        };
    }
}
