package com.drakkarpress.platform.rate;

import com.drakkarpress.platform.security.JwtUserPrincipal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    private static class Counter { int count; long resetEpochMillis; }
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object enforce(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            throw new RateLimitExceededException("No autenticado");
        }
        int effectiveLimit = computeEffectiveLimit(principal.role(), principal.userId(), rateLimit);
        String compositeKey = principal.userId() + ":" + rateLimit.key() + ":" + effectiveLimit;
        long now = Instant.now().toEpochMilli();
        long periodMillis = 24L * 60 * 60 * 1000; // DAY only for now
        Counter ctr = counters.computeIfAbsent(compositeKey, k -> { Counter c = new Counter(); c.count = 0; c.resetEpochMillis = now + periodMillis; return c; });
        if (now > ctr.resetEpochMillis) { ctr.count = 0; ctr.resetEpochMillis = now + periodMillis; }
        if (ctr.count >= effectiveLimit) {
            throw new RateLimitExceededException("Límite diario alcanzado para " + rateLimit.key());
        }
        ctr.count++;
        return pjp.proceed();
    }

    private int computeEffectiveLimit(String role, java.util.UUID userId, RateLimit base) {
        int baseLimit = base.limit();
        if (role == null) return baseLimit;
        return switch (role.toUpperCase()) {
            case "PREMIUM" -> baseLimit * 3;
            case "PRO" -> baseLimit * 5;
            case "ENTERPRISE" -> baseLimit * 10;
            default -> baseLimit;
        };
    }
}
