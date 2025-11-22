package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, UUID> {
    List<DiscountRule> findByIsActiveTrue();
    List<DiscountRule> findByRuleTypeAndIsActiveTrue(String ruleType);
    Optional<DiscountRule> findByCouponCodeAndIsActiveTrue(String couponCode);
}
