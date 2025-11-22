package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PlatformUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email ya registrado"));
        }
        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .userNumber(System.currentTimeMillis())
                .build();
        user = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getSubscription());
        return ResponseEntity.ok(ApiResponse.ok("Registro exitoso", Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername()
        )));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest request) {
        var userOpt = userRepository.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Credenciales inválidas"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Credenciales inválidas"));
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getSubscription());
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", Map.of(
                "token", token,
            "userId", user.getId(),
            "username", user.getUsername()
        )));
    }

    // Social login demo (Google/Facebook) - flujo mock para interfaz
    @SuppressWarnings("null")
    @PostMapping("/social")
    public ResponseEntity<ApiResponse<Map<String, Object>>> social(@RequestBody SocialLoginRequest request) {
        String provider = request.provider().toLowerCase();
        if (!(provider.equals("google") || provider.equals("facebook"))) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Proveedor no soportado"));
        }
        if (request.externalToken() == null || request.externalToken().length() < 5) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token externo inválido"));
        }

        // Si no llega email lo generamos determinísticamente para que el mismo usuario se reutilice
        final String email = (request.email() == null || request.email().isBlank())
            ? provider + ":" + Math.abs(request.externalToken().hashCode()) + "@social.drakkar"
            : request.email();
        final String username = (request.username() == null || request.username().isBlank())
            ? provider + "User"
            : request.username();

        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = userRepository.save(User.builder()
                    .email(email)
                    .username(username)
                    .passwordHash(passwordEncoder.encode("SOCIAL:" + provider))
                    .userNumber(System.currentTimeMillis())
                    .build());
            }

            String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getSubscription());
            return ResponseEntity.ok(ApiResponse.ok("Social login exitoso", Map.of(
                    "token", token,
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "provider", provider
            )));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Error en social login: " + e.getMessage()));
        }
    }

    public record RegisterRequest(String email, String username, String password) {}
    public record LoginRequest(String email, String password) {}
    public record SocialLoginRequest(String provider, String externalToken, String email, String username) {}
}
// Duplicate legacy AuthController removed during JWT migration.
