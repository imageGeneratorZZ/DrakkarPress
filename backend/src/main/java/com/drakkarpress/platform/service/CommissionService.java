package com.drakkarpress.platform.service;

import com.drakkarpress.model.Sale;
import com.drakkarpress.platform.repository.CommissionConfigRepository;
import com.drakkarpress.platform.model.CommissionConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;

@Service
public class CommissionService {

    private final CommissionConfigRepository repository;

    public CommissionService(CommissionConfigRepository repository) {
        this.repository = repository;
    }

    public void applyCommissions(Sale sale, int authorVolumeYtd, int resellerVolumeYtd) {
        String context = resolveContext(sale);
        var configs = repository.findByContextAndIsActiveTrueOrderByMinVolumeAsc(context);
        CommissionConfig chosen = configs.stream()
                .filter(CommissionConfig::isCurrentlyEffective)
                .filter(c -> c.getMinVolume() == null || authorVolumeYtd >= c.getMinVolume())
                .max(Comparator.comparingInt(c -> c.getMinVolume() == null ? 0 : c.getMinVolume()))
                .orElseGet(() -> fallback(context));

        BigDecimal amount = sale.getAmount();
        if (amount == null) return;

        BigDecimal authorPct = pct(chosen.getAuthorPercent());
        BigDecimal resellerPct = pct(chosen.getResellerPercent());
        // BigDecimal printShopPct = pct(chosen.getPrintShopPercent()); // reservado para futura integración
        BigDecimal platformPct = pct(chosen.getPlatformPercent());

        sale.setCommissionAuthor(amount.multiply(authorPct));
        sale.setCommissionReseller(resellerPct.signum() == 0 ? BigDecimal.ZERO : amount.multiply(resellerPct));
        sale.setCommissionPlatform(amount.multiply(platformPct));
        // printShopPct reservado para futura integración
    }

    private CommissionConfig fallback(String context) {
        if ("DIRECT".equals(context)) {
            return CommissionConfig.builder()
                    .context(context)
                    .authorPercent(new BigDecimal("90"))
                    .platformPercent(new BigDecimal("10"))
                    .resellerPercent(BigDecimal.ZERO)
                    .printShopPercent(BigDecimal.ZERO)
                    .isActive(true).build();
        }
        if ("RESELLER".equals(context)) {
            return CommissionConfig.builder()
                    .context(context)
                    .authorPercent(new BigDecimal("60"))
                    .resellerPercent(new BigDecimal("30"))
                    .platformPercent(new BigDecimal("10"))
                    .printShopPercent(BigDecimal.ZERO)
                    .isActive(true).build();
        }
        return CommissionConfig.builder()
                .context(context)
                .authorPercent(new BigDecimal("50"))
                .printShopPercent(new BigDecimal("30"))
                .resellerPercent(new BigDecimal("10"))
                .platformPercent(new BigDecimal("10"))
                .isActive(true).build();
    }

    private String resolveContext(Sale sale) {
        if (!sale.getIsDirect() && sale.getReseller() != null) return "RESELLER";
        return "DIRECT";
    }

    private BigDecimal pct(BigDecimal percent) {
        if (percent == null) return BigDecimal.ZERO;
        return percent.divide(new BigDecimal("100"));
    }
}
