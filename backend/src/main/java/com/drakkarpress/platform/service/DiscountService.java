package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.DiscountRule;
import com.drakkarpress.platform.repository.DiscountRuleRepository;
import com.drakkarpress.platform.model.Membership;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiscountService {

    private final DiscountRuleRepository repository;

    public DiscountService(DiscountRuleRepository repository) {
        this.repository = repository;
    }

    public BigDecimal applyDiscounts(BigDecimal baseAmount, Membership membership, int quantity, String coupon) {
        if (baseAmount == null) return BigDecimal.ZERO;
        List<DiscountRule> active = repository.findByIsActiveTrue();
        BigDecimal amount = baseAmount;
        for (DiscountRule rule : active) {
            if (!Boolean.TRUE.equals(rule.getIsActive())) continue;
            switch (rule.getRuleType()) {
                case "PHASE":
                    if (membership != null && rule.getPhase() != null && membership.getPlan().contains(rule.getPhase())) {
                        amount = applyPercentOff(amount, rule.getPercentOff());
                    }
                    break;
                case "VOLUME":
                    if (rule.getMinQuantity() != null && quantity >= rule.getMinQuantity()) {
                        amount = applyPercentOff(amount, rule.getPercentOff());
                    }
                    break;
                case "COUPON":
                    if (coupon != null && rule.getCouponCode() != null && coupon.equalsIgnoreCase(rule.getCouponCode())) {
                        amount = applyPercentOff(amount, rule.getPercentOff());
                    }
                    break;
                case "COURTESY":
                    if (membership != null && "PREMIUM_COURTESY".equals(membership.getPlan())) {
                        amount = applyPercentOff(amount, rule.getPercentOff());
                    }
                    break;
                default:
                    break;
            }
        }
        return amount;
    }

    private BigDecimal applyPercentOff(BigDecimal amount, BigDecimal percent) {
        if (percent == null) return amount;
        return amount.subtract(amount.multiply(percent).divide(new BigDecimal("100")));
    }
}
