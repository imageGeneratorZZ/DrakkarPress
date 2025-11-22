package com.drakkarpress.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class AuthUserResolver {

    private static final ThreadLocal<UUID> currentUser = new ThreadLocal<>();

    public UUID getCurrentUserId() {
        UUID fromThread = currentUser.get();
        if (fromThread != null) return fromThread;
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest req = attrs.getRequest();
        String header = req.getHeader("X-User-Id");
        if (header == null || header.isBlank()) return null;
        try { return UUID.fromString(header); } catch (Exception e) { return null; }
    }

    public static void setCurrentUser(UUID userId) { currentUser.set(userId); }
    public static void clear() { currentUser.remove(); }
}
