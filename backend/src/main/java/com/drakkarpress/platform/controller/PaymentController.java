package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.dto.CheckoutRequest;
import com.drakkarpress.platform.dto.CheckoutResponse;
import com.drakkarpress.platform.dto.PaymentHistoryResponse;
import com.drakkarpress.platform.model.PaymentTransaction;
import com.drakkarpress.platform.security.JwtTokenProvider;
import com.drakkarpress.platform.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador de Pagos (Integración Shopify)
 *
 * Endpoints:
 * - POST /api/payments/create-checkout : genera transacción y URL de Shopify
 * - POST /api/payments/webhook : recepción de eventos Shopify (HMAC)
 * - GET /api/payments/history : historial del usuario
 * - GET /api/payments/health : verificación servicio
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtTokenProvider tokenProvider;

    /**
     * Crear checkout de membresía (redirección a Shopify)
     */
    @PostMapping("/create-checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CheckoutRequest request) {
        
        try {
            // Extraer userId del token
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);

            log.info("Creando checkout para usuario: {} - Plan: {} - Frecuencia: {}", 
                     userId, request.getPlanType(), request.getFrequency());

                // Crear checkout (transacción + URL Shopify)
            Map<String, Object> sessionData = paymentService.createCheckoutSession(
                    userId,
                    request.getPlanType(),
                    request.getFrequency()
            );

            CheckoutResponse response = CheckoutResponse.builder()
                    .sessionId(null) // No se usa sesión, solo URL de Shopify
                    .checkoutUrl((String) sessionData.get("checkoutUrl"))
                    .transactionId((UUID) sessionData.get("transactionId"))
                    .status("REDIRECT_TO_SHOPIFY")
                    .build();

                return ResponseEntity.ok(ApiResponse.success("Checkout Shopify creado", response));

        } catch (Exception e) {
            log.error("Error creando checkout: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear sesión de pago: " + e.getMessage()));
        }
    }

        /**
         * Webhook de Shopify (HMAC verificación pendiente)
         */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmacHeader) {
        
        try {
            log.info("Webhook recibido de Shopify");

            paymentService.handleWebhook(payload, hmacHeader);
            
            return ResponseEntity.ok("Webhook procesado");

        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Error";
            if (msg.contains("Firma inválida") || msg.contains("HMAC")) {
                log.warn("Webhook rechazado por firma inválida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
            }
            log.error("Error procesando webhook: {}", msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando webhook");
        }
    }

    /**
     * Obtener historial de pagos
     * 
     * GET /api/payments/history
     * Authorization: Bearer token
     * 
     * Response: {
     *   "success": true,
     *   "message": "Historial obtenido",
     *   "data": [
     *     {
     *       "id": "uuid",
     *       "amount": 49.00,
     *       "currency": "USD",
     *       "paymentStatus": "COMPLETED",
     *       "planName": "Fundador",
     *       "createdAt": "2025-01-15T10:30:00",
     *       ...
     *     }
     *   ]
     * }
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentHistoryResponse>>> getPaymentHistory(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            // Extraer userId del token
            String token = authHeader.substring(7);
            UUID userId = tokenProvider.getUserIdFromToken(token);

            List<PaymentTransaction> transactions = paymentService.getPaymentHistory(userId);
            
            List<PaymentHistoryResponse> history = transactions.stream()
                    .map(PaymentHistoryResponse::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Historial de pagos obtenido", history));

        } catch (Exception e) {
            log.error("Error obteniendo historial: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener historial de pagos"));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Payment service is healthy", "OK"));
    }
}
