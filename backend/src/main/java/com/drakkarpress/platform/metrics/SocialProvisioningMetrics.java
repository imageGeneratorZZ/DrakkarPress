package com.drakkarpress.platform.metrics;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Métricas simples en memoria para provisión social.
 * Se puede extender a Micrometer posteriormente.
 */
@Component
public class SocialProvisioningMetrics {
    private final AtomicLong newUsers = new AtomicLong();
    private final AtomicLong existingUsers = new AtomicLong();

    public void incrementNew() {
        newUsers.incrementAndGet();
    }

    public void incrementExisting() {
        existingUsers.incrementAndGet();
    }

    public long getNewUsers() {
        return newUsers.get();
    }

    public long getExistingUsers() {
        return existingUsers.get();
    }
}