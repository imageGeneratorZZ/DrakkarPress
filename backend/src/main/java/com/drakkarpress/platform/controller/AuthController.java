package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.dto.request.RefreshTokenRequest;
import com.drakkarpress.platform.dto.request.RegisterRequest;
import com.drakkarpress.platform.dto.response.AuthResponse;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.security.JwtTokenProvider;
import com.drakkarpress.platform.service.AuthService;
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
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email ya registrado"));
            }
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username ya registrado"));
            }
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .fullName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .userNumber(System.currentTimeMillis())
                    .build();
            user = userRepository.save(user);
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole(), user.getSubscription());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            authService.initializeSession(user.getId(), accessToken, refreshToken);
            return ResponseEntity.ok(ApiResponse.ok("Registro exitoso", Map.of(
                    "token", accessToken,
                    "refreshToken", refreshToken,
                    "userId", user.getId(),
                    "username", user.getUsername()
            )));
        } catch (Exception e) {
            System.err.println("[REGISTER] Error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("Error interno registro: " + e.getMessage()));
        }
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
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole(), user.getSubscription());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        authService.initializeSession(user.getId(), accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", Map.of(
                "token", accessToken,
                "refreshToken", refreshToken,
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

                String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole(), user.getSubscription());
                String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
                authService.initializeSession(user.getId(), accessToken, refreshToken);
            return ResponseEntity.ok(ApiResponse.ok("Social login exitoso", Map.of(
                    "token", accessToken,
                    "refreshToken", refreshToken,
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "provider", provider
            )));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Error en social login: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refresh(@RequestBody RefreshRequest request) {
        try {
            RefreshTokenRequest req = new RefreshTokenRequest();
            req.setRefreshToken(request.refreshToken());
            AuthResponse authResp = authService.refreshToken(req);
            return ResponseEntity.ok(ApiResponse.ok("Token renovado", Map.of(
                    "token", authResp.getAccessToken(),
                    "refreshToken", authResp.getRefreshToken(),
                    "userId", authResp.getUserId(),
                    "username", authResp.getUsername()
            )));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Error al renovar token: " + e.getMessage()));
        }
    }

    public record LoginRequest(String email, String password) {}
    public record SocialLoginRequest(String provider, String externalToken, String email, String username) {}
    public record RefreshRequest(String refreshToken) {}
}
// Duplicate legacy AuthController removed during JWT migration.
