package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PaymentTransactionRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@TestPropertySource(properties = {
        "shopify.webhook.secret=test-secret"
})
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private PlatformUserRepository userRepository;

    private String hmac(String secret, String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes()));
    }

    @Test
    void handleWebhook_marksTransactionCompleted_onPaidOrder() throws Exception {
        // Crear usuario y transacción inicial (checkout simulado)
        User user = User.builder()
                .email("test-user@drakkarpress.com")
                .username("test-user")
                .userNumber(1001L)
                .passwordHash("test-hash")
                .isActive(true)
                .isEmailVerified(true)
                .build();
        user = userRepository.save(user); Assertions.assertNotNull(user.getId());

        Map<String, Object> checkout = paymentService.createCheckoutSession(
                user.getId(),
                "PREMIUM_PHASE_1",
                "ANNUAL"
        );

        UUID transactionId = (UUID) checkout.get("transactionId");
        Assertions.assertNotNull(transactionId);

        // Construir payload de Shopify con financial_status paid y note = transactionId
        String payload = "{\"id\":\"ORDER123\",\"financial_status\":\"paid\",\"note\":\"" + transactionId + "\"}";
        String signature = hmac("test-secret", payload);

        paymentService.handleWebhook(payload, signature);

        PaymentTransaction tx = transactionRepository.findById(transactionId).orElseThrow();
        Assertions.assertEquals("COMPLETED", tx.getPaymentStatus(), "La transacción debe marcarse como COMPLETED");
        Assertions.assertEquals("ORDER123", tx.getExternalTransactionId(), "Debe guardar el id externo del pedido");
    }

    @Test
    void handleWebhook_invalidHmac_throwsException() {
        String payload = "{\"id\":\"ORDER999\",\"financial_status\":\"paid\",\"note\":\"" + UUID.randomUUID() + "\"}";
        String badSignature = "invalid-signature";
        Assertions.assertThrows(RuntimeException.class, () -> paymentService.handleWebhook(payload, badSignature));
    }
}
