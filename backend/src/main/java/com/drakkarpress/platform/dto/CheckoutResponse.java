package com.drakkarpress.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para respuesta de checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private String sessionId;
    private String checkoutUrl;
    private UUID transactionId;
    private String status;
}
