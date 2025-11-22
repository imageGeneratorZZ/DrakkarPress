package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.MembershipRepository;
import com.drakkarpress.platform.repository.PaymentTransactionRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servicio de Pagos con Shopify
 * Todos los pagos se procesan a través de Shopify
 */
@Service
@Slf4j
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final PlatformUserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final EmailService emailService;
    private final PricingService pricingService;

    @Value("${shopify.store.url:https://drakkarpress.myshopify.com}")
    private String shopifyStoreUrl;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${shopify.webhook.secret:dummy-webhook-secret}")
    private String shopifyWebhookSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentService(
            PaymentTransactionRepository paymentRepository,
            PlatformUserRepository userRepository,
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
     * Redirige a Shopify para el checkout
     */
    @Transactional
    public Map<String, Object> createCheckoutSession(UUID userId, String planType, String frequency) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            PricingService.PricingInfo pricing = pricingService.getCurrentPricing(planType, frequency);
            long amountInCents = pricing.getPriceInCents();

            PaymentTransaction transaction = PaymentTransaction.createSignup(
                    user,
                    "SHOPIFY",
                    new BigDecimal(amountInCents).divide(new BigDecimal(100)),
                    planType,
                    frequency
            );
            transaction = paymentRepository.save(transaction);

            String checkoutUrl = shopifyStoreUrl + "/cart?note=" + transaction.getId();

            Map<String, Object> response = new HashMap<>();
            response.put("checkoutUrl", checkoutUrl);
            response.put("transactionId", transaction.getId());
            response.put("shopifyStoreUrl", shopifyStoreUrl);

            log.info("Checkout creado - Transacción: {}", transaction.getId());
            return response;

        } catch (Exception e) {
            log.error("Error creando checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Error creando sesión de pago");
        }
    }

    /**
     * Webhook de Shopify
     */
    @Transactional
    public void handleWebhook(String payload, String hmacHeader) {
        try {
            if (hmacHeader == null || hmacHeader.isBlank()) {
                log.warn("Webhook Shopify sin HMAC header");
                throw new RuntimeException("HMAC faltante");
            }

            if (!verifyHmac(payload, hmacHeader)) {
                log.warn("HMAC inválido para webhook Shopify");
                throw new RuntimeException("Firma inválida");
            }

            log.info("Webhook de Shopify verificado correctamente");

            // Parsear JSON para detectar evento
            JsonNode root = objectMapper.readTree(payload);
            // Ejemplo: order paid -> root.get("financial_status") == "paid"
            String financialStatus = getText(root, "financial_status");
            String orderId = getText(root, "id");

            // Extraer transaction UUID desde note (lo guardamos como ?note=<uuid>)
            String note = getText(root, "note");
            UUID transactionId = null;
            if (note != null) {
                try { transactionId = UUID.fromString(note.trim()); } catch (IllegalArgumentException ignored) { }
            }

            if ("paid".equalsIgnoreCase(financialStatus) && transactionId != null) {
                log.info("Procesando pago completado Shopify. orderId={} transactionId={}", orderId, transactionId);
                completeTransaction(transactionId, orderId);
            } else {
                log.info("Evento Shopify ignorado. financial_status={} note={} orderId={}", financialStatus, note, orderId);
            }
        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Error procesando webhook");
        }
    }

    /**
     * Activar membresía después de pago
     */
    private void activateMembership(User user, PaymentTransaction transaction) {
        String frequency = transaction.getPaymentFrequency();
        LocalDateTime expiresAt = calculateExpirationDate(frequency);

        Optional<Membership> existingMembership = membershipRepository.findByUserId(user.getId());
        Membership membership;

        if (existingMembership.isPresent()) {
            membership = existingMembership.get();
            membership.setStatus("ACTIVE");
            membership.setExpiresAt(expiresAt);
            membership.setPaymentFrequency(frequency);
        } else {
            membership = Membership.builder()
                    .user(user)
                    .status("ACTIVE")
                    .expiresAt(expiresAt)
                    .paymentFrequency(frequency)
                    .build();
        }

        membershipRepository.save(membership);
        transaction.setMembershipId(membership.getId());
        paymentRepository.save(transaction);

        log.info("Membresía activada para usuario: {}", user.getEmail());
    }

    /**
     * Calcular fecha de expiración
     */
    private LocalDateTime calculateExpirationDate(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        return switch (frequency) {
            case "MONTHLY" -> now.plusMonths(1);
            case "ANNUAL" -> now.plusYears(1);
            case "LIFETIME" -> now.plusYears(100);
            default -> now.plusMonths(1);
        };
    }

    /**
     * Historial de pagos
     */
    public List<PaymentTransaction> getPaymentHistory(UUID userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

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

    // ===================== NUEVAS UTILIDADES SHOPIFY =====================

    private boolean verifyHmac(String payload, String hmacHeader) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(shopifyWebhookSecret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] digest = mac.doFinal(payload.getBytes());
            String computed = Base64.getEncoder().encodeToString(digest);
            return computed.equals(hmacHeader.trim());
        } catch (Exception e) {
            log.error("Error verificando HMAC Shopify: {}", e.getMessage());
            return false;
        }
    }

    private String getText(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode child = node.get(field);
        return (child != null && !child.isNull()) ? child.asText() : null;
    }

    @Transactional
    protected void completeTransaction(UUID transactionId, String externalOrderId) {
        paymentRepository.findById(transactionId).ifPresent(tx -> {
            if (!tx.isCompleted()) {
                tx.markCompleted(externalOrderId);
                paymentRepository.save(tx);
                // Activar membresía si corresponde
                activateMembership(tx.getUser(), tx);
                try {
                    emailService.sendMembershipActivatedEmail(tx.getUser().getEmail(), tx.getPlanType());
                } catch (Exception ex) {
                    log.warn("No se pudo enviar email de activación: {}", ex.getMessage());
                }
                log.info("Transacción {} marcada como COMPLETED via Shopify order {}", transactionId, externalOrderId);
            }
        });
    }
}
