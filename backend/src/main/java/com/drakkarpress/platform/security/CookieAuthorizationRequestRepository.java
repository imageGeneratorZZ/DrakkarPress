package com.drakkarpress.platform.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Almacena OAuth2AuthorizationRequest en una cookie para flujos stateless.
 * Permite validar el parámetro 'state' en el success handler.
 */
@Component
public class CookieAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String AUTH_REQUEST_COOKIE_NAME = "OAUTH2_AUTH_REQUEST";
    private static final int COOKIE_EXPIRE_SECONDS = 300; // 5 min

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (AUTH_REQUEST_COOKIE_NAME.equals(c.getName())) {
                try {
                    String decoded = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    // Simple format: state|clientId|redirectUri
                    String[] parts = decoded.split("\\|", 3);
                    if (parts.length < 1) return null;
                    // Sólo necesitamos el state para validación; devolvemos null si no vamos a reconstruir todo.
                    return OAuth2AuthorizationRequest.authorizationCode()
                            .state(parts[0])
                            .authorizationUri("N/A")
                            .clientId(parts.length > 1 ? parts[1] : "N/A")
                            .redirectUri(parts.length > 2 ? parts[2] : "N/A")
                            .build();
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(response);
            return;
        }
        String raw = authorizationRequest.getState() + "|" + authorizationRequest.getClientId() + "|" + authorizationRequest.getRedirectUri();
        String encoded = URLEncoder.encode(raw, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(AUTH_REQUEST_COOKIE_NAME, encoded);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        response.addCookie(cookie);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest req = loadAuthorizationRequest(request);
        removeAuthorizationRequestCookies(response);
        return req;
    }

    public void removeAuthorizationRequestCookies(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_REQUEST_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}