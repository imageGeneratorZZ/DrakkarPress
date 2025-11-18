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
 * Controlador de Pagos
 * 
 * Endpoints:
 * - POST /api/payments/create-checkout - Crear sesión de pago
 * - POST /api/payments/webhook - Webhook de Stripe
 * - GET /api/payments/history - Historial de pagos
 * - GET /api/payments/session/{sessionId} - Estado de sesión
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtTokenProvider tokenProvider;

    /**
     * Crear sesión de checkout
     * 
     * POST /api/payments/create-checkout
     * Authorization: Bearer token
     * Body: { "planType": "PREMIUM_PHASE_1", "frequency": "ANNUAL" }
     * 
     * Response: {
     *   "success": true,
     *   "message": "Sesión creada",
     *   "data": {
     *     "sessionId": "cs_test_...",
     *     "checkoutUrl": "https://checkout.stripe.com/...",
     *     "transactionId": "uuid",
     *     "status": "PENDING"
     *   }
     * }
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

            // Crear sesión
            Map<String, Object> sessionData = paymentService.createCheckoutSession(
                    userId,
                    request.getPlanType(),
                    request.getFrequency()
            );

            CheckoutResponse response = CheckoutResponse.builder()
                    .sessionId((String) sessionData.get("sessionId"))
                    .checkoutUrl((String) sessionData.get("url"))
                    .transactionId((UUID) sessionData.get("transactionId"))
                    .status("PENDING")
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Sesión de pago creada", response));

        } catch (Exception e) {
            log.error("Error creando checkout: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear sesión de pago: " + e.getMessage()));
        }
    }

    /**
     * Webhook de Stripe
     * 
     * POST /api/payments/webhook
     * Stripe-Signature: signature header
     * Body: raw event payload
     * 
     * Este endpoint es llamado automáticamente por Stripe
     * cuando ocurren eventos (pago completado, fallido, etc.)
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        
        try {
            log.info("Webhook recibido de Stripe");
            
            paymentService.handleWebhook(payload, sigHeader);
            
            return ResponseEntity.ok("Webhook procesado");

        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando webhook: " + e.getMessage());
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
     * Verificar estado de sesión
     * 
     * GET /api/payments/session/{sessionId}
     * 
     * Usado después de que el usuario regresa de Stripe Checkout
     * para verificar si el pago fue exitoso
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionStatus(
            @PathVariable String sessionId) {
        
        try {
            Map<String, Object> status = paymentService.getSessionStatus(sessionId);
            return ResponseEntity.ok(ApiResponse.success("Estado de sesión obtenido", status));

        } catch (Exception e) {
            log.error("Error obteniendo sesión: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar estado de pago"));
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
