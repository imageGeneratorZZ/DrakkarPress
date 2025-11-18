package com.drakkarpress.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank(message = "El tipo de plan es requerido")
    @Pattern(regexp = "PREMIUM_PHASE_1|PREMIUM_PHASE_2|PREMIUM_PHASE_3", 
             message = "Tipo de plan inválido")
    private String planType;

    @NotBlank(message = "La frecuencia de pago es requerida")
    @Pattern(regexp = "MONTHLY|ANNUAL|LIFETIME", 
             message = "Frecuencia de pago inválida")
    private String frequency;
}
