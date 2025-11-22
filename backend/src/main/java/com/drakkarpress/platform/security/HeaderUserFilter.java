package com.drakkarpress.platform.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class HeaderUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest http) {
                String header = http.getHeader("X-User-Id");
                if (header != null && !header.isBlank()) {
                    try { AuthUserResolver.setCurrentUser(UUID.fromString(header)); } catch (Exception ignored) {}
                }
            }
            chain.doFilter(request, response);
        } finally {
            AuthUserResolver.clear();
        }
    }
}
