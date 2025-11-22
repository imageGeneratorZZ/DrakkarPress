package com.drakkarpress.platform.config;

import com.drakkarpress.platform.model.CommissionConfig;
import com.drakkarpress.platform.model.DiscountRule;
import com.drakkarpress.platform.repository.CommissionConfigRepository;
import com.drakkarpress.platform.repository.DiscountRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Profile({"h2", "test"}) // Solo para entornos local y test
public class CommissionDiscountSeedInitializer {

    private final CommissionConfigRepository commissionRepo;
    private final DiscountRuleRepository discountRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        System.out.println("\n[SEED-INIT] CommissionDiscountSeedInitializer ejecutándose en perfil h2/test");
        seedCommissionConfigs();
        seedDiscountRules();
    }

    private void seedCommissionConfigs() {
        if (commissionRepo.count() > 0) return;
        // Venta directa base
        CommissionConfig directBase = CommissionConfig.builder()
                .context("DIRECT")
                .authorPercent(new BigDecimal("90"))
                .platformPercent(new BigDecimal("10"))
                .resellerPercent(BigDecimal.ZERO)
                .printShopPercent(BigDecimal.ZERO)
                .minVolume(0)
                .isActive(true)
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .build();
        commissionRepo.save(java.util.Objects.requireNonNull(directBase));
        // Venta directa alto volumen
        CommissionConfig directHigh = CommissionConfig.builder()
                .context("DIRECT")
                .authorPercent(new BigDecimal("92"))
                .platformPercent(new BigDecimal("8"))
                .resellerPercent(BigDecimal.ZERO)
                .printShopPercent(BigDecimal.ZERO)
                .minVolume(1000)
                .isActive(true)
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .build();
        commissionRepo.save(java.util.Objects.requireNonNull(directHigh));
        // Con revendedor
        CommissionConfig resellerBase = CommissionConfig.builder()
                .context("RESELLER")
                .authorPercent(new BigDecimal("60"))
                .resellerPercent(new BigDecimal("30"))
                .platformPercent(new BigDecimal("10"))
                .printShopPercent(BigDecimal.ZERO)
                .minVolume(0)
                .isActive(true)
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .build();
        commissionRepo.save(java.util.Objects.requireNonNull(resellerBase));
        // Con revendedor alto volumen
        CommissionConfig resellerHigh = CommissionConfig.builder()
                .context("RESELLER")
                .authorPercent(new BigDecimal("62"))
                .resellerPercent(new BigDecimal("28"))
                .platformPercent(new BigDecimal("10"))
                .printShopPercent(BigDecimal.ZERO)
                .minVolume(1500)
                .isActive(true)
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .build();
        commissionRepo.save(java.util.Objects.requireNonNull(resellerHigh));
    }

    private void seedDiscountRules() {
        if (discountRepo.count() > 0) return;
        // Fase founder (plan contiene 'FOUNDER')
        DiscountRule phaseFounder = DiscountRule.builder()
                .ruleType("PHASE")
                .phase("FOUNDER")
                .percentOff(new BigDecimal("15"))
                .description("Descuento especial fase founder")
                .isActive(true)
                .build();
        discountRepo.save(java.util.Objects.requireNonNull(phaseFounder));
        // Volumen 10+ unidades
        DiscountRule volume10 = DiscountRule.builder()
                .ruleType("VOLUME")
                .minQuantity(10)
                .percentOff(new BigDecimal("5"))
                .description("Volumen >=10 aplica 5%")
                .isActive(true)
                .build();
        discountRepo.save(java.util.Objects.requireNonNull(volume10));
        // Cupón SAVE10
        DiscountRule couponSave10 = DiscountRule.builder()
                .ruleType("COUPON")
                .couponCode("SAVE10")
                .percentOff(new BigDecimal("10"))
                .description("Cupón SAVE10 10% off")
                .isActive(true)
                .build();
        discountRepo.save(java.util.Objects.requireNonNull(couponSave10));
        // Cortesía membresía premium
        DiscountRule courtesy = DiscountRule.builder()
                .ruleType("COURTESY")
                .percentOff(new BigDecimal("100"))
                .description("Acceso cortesía: 100% descuento")
                .isActive(true)
                .build();
        discountRepo.save(java.util.Objects.requireNonNull(courtesy));
    }
}
