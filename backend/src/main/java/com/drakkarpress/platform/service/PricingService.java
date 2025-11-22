package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio para calcular precios de membresía según fase de grandfathering
 */
@Service
public class PricingService {

    /**
     * Calcular precio de membresía según user_number
     * 
     * PROMOCIÓN DE LANZAMIENTO:
     * - Fase 1 (1-1,000): $5/mes ($50/año → $30/año con 40% descuento) GRANDFATHERED
     * - Fase 2 (1,001-10,000): $10/mes ($100/año → $60/año con 40% descuento) GRANDFATHERED
     * - Fase 3 (10,001-15,000): $15/mes ($150/año → $90/año con 40% descuento) GRANDFATHERED
     * - Regular (15,001+): $19.90/mes ($170/año → $102/año con 40% descuento)
     * 
     * COMISIONES DE VENTAS:
     * - Usuarios FREE: 25% comisión plataforma
     * - Usuarios PREMIUM: 5% comisión plataforma
     */
    public PricingInfo calculatePricing(Long userNumber) {
        if (userNumber == null) {
            return new PricingInfo("FREE", BigDecimal.ZERO, BigDecimal.ZERO, false, null);
        }

        if (userNumber >= 1 && userNumber <= 1000) {
            return new PricingInfo(
                "PREMIUM_PHASE_1",
                new BigDecimal("5.00"),    // Mensual
                new BigDecimal("50.00"),   // Anual
                true,                      // Grandfathered
                "FOUNDER"                  // Badge
            );
        }

        if (userNumber >= 1001 && userNumber <= 10000) {
            return new PricingInfo(
                "PREMIUM_PHASE_2",
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                true,
                "EARLY_ADOPTER"
            );
        }

        if (userNumber >= 10001 && userNumber <= 15000) {
            return new PricingInfo(
                "PREMIUM_PHASE_3",
                new BigDecimal("15.00"),
                new BigDecimal("150.00"),
                true,
                "LAUNCH_MEMBER"
            );
        }

        // Regular (15,001+)
        return new PricingInfo(
            "PREMIUM_REGULAR",
            new BigDecimal("19.90"),
            new BigDecimal("170.00"),
            false,
            "PREMIUM_MEMBER"
        );
    }

    /**
     * Obtener mensaje personalizado según fase
     */
    public String getWelcomeMessage(Long userNumber) {
        if (userNumber == null) {
            return "Bienvenido a DrakkarPress!";
        }

        if (userNumber <= 1000) {
            return String.format(
                "¡Felicidades! Eres FUNDADOR #%d. Tu precio de $50/año está bloqueado para siempre. " +
                "Recibirás el badge exclusivo 'Founder' que nadie más podrá obtener.",
                userNumber
            );
        }

        if (userNumber <= 10000) {
            return String.format(
                "¡Bienvenido Early Adopter #%d! Tu precio de $100/año está bloqueado de por vida. " +
                "Recibirás el badge exclusivo 'Early Adopter'.",
                userNumber
            );
        }

        if (userNumber <= 15000) {
            return String.format(
                "¡Bienvenido! Eres el usuario #%d. Tu precio de $150/año está bloqueado de por vida. " +
                "Recibirás el badge exclusivo 'Launch Member'.",
                userNumber
            );
        }

        return String.format(
            "¡Bienvenido! Eres el usuario #%d. Tu precio regular es $19.90/mes o $170/año.",
            userNumber
        );
    }

    /**
     * Calcular cupos restantes en fase actual
     */
    public SlotsInfo getRemainingSlots(Long currentMaxUserNumber) {
        if (currentMaxUserNumber == null || currentMaxUserNumber < 1000) {
            long remaining = 1000 - (currentMaxUserNumber != null ? currentMaxUserNumber : 0);
            return new SlotsInfo("PHASE_1", "Fundador", remaining, 1000, new BigDecimal("50.00"));
        }

        if (currentMaxUserNumber < 10000) {
            long remaining = 10000 - currentMaxUserNumber;
            return new SlotsInfo("PHASE_2", "Early Adopter", remaining, 9000, new BigDecimal("100.00"));
        }

        if (currentMaxUserNumber < 15000) {
            long remaining = 15000 - currentMaxUserNumber;
            return new SlotsInfo("PHASE_3", "Launch Promo", remaining, 5000, new BigDecimal("150.00"));
        }

        return new SlotsInfo("REGULAR", "Premium", -1, -1, new BigDecimal("170.00"));
    }

    /**
     * Obtener pricing actual según plan y frecuencia
     */
    public PricingInfo getCurrentPricing(String planType, String frequency) {
        // Por ahora retornamos precios regulares, pero esto debería integrarse con calculatePricing
        BigDecimal monthlyPrice = new BigDecimal("19.90");
        BigDecimal annualPrice = new BigDecimal("170.00");
        
        return new PricingInfo("PREMIUM_REGULAR", monthlyPrice, annualPrice, false, "PREMIUM_MEMBER");
    }

    /**
     * DTO de información de precio
     */
    public static class PricingInfo {
        public final String plan;
        public final BigDecimal monthlyPrice;
        public final BigDecimal annualPrice;
        public final boolean isGrandfathered;
        public final String badge;

        public PricingInfo(String plan, BigDecimal monthlyPrice, BigDecimal annualPrice, 
                          boolean isGrandfathered, String badge) {
            this.plan = plan;
            this.monthlyPrice = monthlyPrice;
            this.annualPrice = annualPrice;
            this.isGrandfathered = isGrandfathered;
            this.badge = badge;
        }

        public long getPriceInCents() {
            return annualPrice.multiply(new BigDecimal("100")).longValue();
        }
    }

    /**
     * DTO de información de cupos
     */
    public static class SlotsInfo {
        public final String phase;
        public final String phaseName;
        public final long remainingSlots;
        public final long totalSlots;
        public final BigDecimal annualPrice;

        public SlotsInfo(String phase, String phaseName, long remainingSlots, 
                        long totalSlots, BigDecimal annualPrice) {
            this.phase = phase;
            this.phaseName = phaseName;
            this.remainingSlots = remainingSlots;
            this.totalSlots = totalSlots;
            this.annualPrice = annualPrice;
        }
    }
}
