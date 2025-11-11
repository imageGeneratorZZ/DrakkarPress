package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.response.ApiResponse;
import com.drakkarpress.platform.repository.UserRepository;
import com.drakkarpress.platform.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador público para información de precios y disponibilidad de fases
 */
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtener información de todos los planes
     * GET /api/pricing/plans
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlans() {
        Map<String, Object> plans = new HashMap<>();
        
        // Plan FREE
        Map<String, Object> free = new HashMap<>();
        free.put("name", "FREE");
        free.put("monthlyPrice", 0);
        free.put("annualPrice", 0);
        free.put("features", new String[]{
            "Acceso básico",
            "10 generaciones PDF/mes",
            "Sin runa personalizada"
        });
        plans.put("FREE", free);

        // Fase 1: Fundadores
        Map<String, Object> phase1 = new HashMap<>();
        phase1.put("name", "FUNDADOR");
        phase1.put("userRange", "1-1,000");
        phase1.put("monthlyPrice", 5.00);
        phase1.put("annualPrice", 50.00);
        phase1.put("savings", "17%");
        phase1.put("isGrandfathered", true);
        phase1.put("badge", "Founder");
        phase1.put("features", new String[]{
            "Todo lo Premium",
            "Precio bloqueado de por vida",
            "Badge exclusivo Founder",
            "Acceso prioritario a nuevas funciones",
            "Mención en Hall of Fame"
        });
        plans.put("PHASE_1", phase1);

        // Fase 2: Early Adopters
        Map<String, Object> phase2 = new HashMap<>();
        phase2.put("name", "EARLY ADOPTER");
        phase2.put("userRange", "1,001-10,000");
        phase2.put("monthlyPrice", 10.00);
        phase2.put("annualPrice", 100.00);
        phase2.put("savings", "17%");
        phase2.put("isGrandfathered", true);
        phase2.put("badge", "Early Adopter");
        phase2.put("features", new String[]{
            "Todo lo Premium",
            "Precio bloqueado de por vida",
            "Badge exclusivo Early Adopter",
            "Acceso temprano a nuevas funciones"
        });
        plans.put("PHASE_2", phase2);

        // Fase 3: Launch Promo
        Map<String, Object> phase3 = new HashMap<>();
        phase3.put("name", "LAUNCH PROMO");
        phase3.put("userRange", "10,001-15,000");
        phase3.put("monthlyPrice", 15.00);
        phase3.put("annualPrice", 150.00);
        phase3.put("savings", "17%");
        phase3.put("isGrandfathered", true);
        phase3.put("badge", "Launch Member");
        phase3.put("features", new String[]{
            "Todo lo Premium",
            "Precio bloqueado de por vida",
            "Badge exclusivo Launch Member"
        });
        plans.put("PHASE_3", phase3);

        // Regular
        Map<String, Object> regular = new HashMap<>();
        regular.put("name", "PREMIUM");
        regular.put("userRange", "15,001+");
        regular.put("monthlyPrice", 19.90);
        regular.put("annualPrice", 170.00);
        regular.put("savings", "15%");
        regular.put("isGrandfathered", false);
        regular.put("badge", "Premium Member");
        regular.put("features", new String[]{
            "Generación ilimitada de PDFs",
            "Selección de runa personalizada",
            "Acceso a todos los generadores",
            "Red social completa",
            "Mensajería privada",
            "Soporte prioritario"
        });
        plans.put("REGULAR", regular);

        return ResponseEntity.ok(ApiResponse.success("Pricing plans retrieved", plans));
    }

    /**
     * Obtener fase actual y cupos disponibles
     * GET /api/pricing/current-phase
     */
    @GetMapping("/current-phase")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentPhase() {
        Long maxUserNumber = userRepository.findMaxUserNumber().orElse(0L);
        PricingService.SlotsInfo slotsInfo = pricingService.getRemainingSlots(maxUserNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("currentPhase", slotsInfo.phase);
        response.put("phaseName", slotsInfo.phaseName);
        response.put("currentUserCount", maxUserNumber);
        response.put("annualPrice", slotsInfo.annualPrice);
        
        if (slotsInfo.remainingSlots > 0) {
            response.put("remainingSlots", slotsInfo.remainingSlots);
            response.put("totalSlots", slotsInfo.totalSlots);
            response.put("urgency", getUrgencyMessage(slotsInfo.remainingSlots, slotsInfo.totalSlots));
        } else {
            response.put("message", "Precio regular - Sin límite de cupos");
        }

        return ResponseEntity.ok(ApiResponse.success("Current phase info", response));
    }

    /**
     * Calcular precio para un usuario específico (simulación)
     * GET /api/pricing/calculate?userNumber=500
     */
    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculatePrice(
            @RequestParam(required = false) Long userNumber) {
        
        if (userNumber == null) {
            userNumber = userRepository.findMaxUserNumber().orElse(0L) + 1;
        }

        PricingService.PricingInfo pricing = pricingService.calculatePricing(userNumber);
        String welcomeMessage = pricingService.getWelcomeMessage(userNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("userNumber", userNumber);
        response.put("plan", pricing.plan);
        response.put("monthlyPrice", pricing.monthlyPrice);
        response.put("annualPrice", pricing.annualPrice);
        response.put("isGrandfathered", pricing.isGrandfathered);
        response.put("badge", pricing.badge);
        response.put("welcomeMessage", welcomeMessage);

        return ResponseEntity.ok(ApiResponse.success("Pricing calculated", response));
    }

    /**
     * Mensaje de urgencia según cupos restantes
     */
    private String getUrgencyMessage(long remaining, long total) {
        double percentage = (double) remaining / total * 100;
        
        if (percentage <= 5) {
            return "¡ÚLTIMOS CUPOS! Solo quedan " + remaining + " lugares en esta fase";
        } else if (percentage <= 20) {
            return "¡Quedan pocos cupos! " + remaining + " lugares disponibles";
        } else if (percentage <= 50) {
            return remaining + " cupos disponibles en esta fase";
        } else {
            return "Fase activa - " + remaining + " cupos disponibles";
        }
    }
}
