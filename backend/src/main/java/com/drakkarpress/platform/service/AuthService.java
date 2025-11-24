package com.drakkarpress.platform.service;

import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.dto.request.LoginRequest;
import com.drakkarpress.platform.dto.request.RefreshTokenRequest;
import com.drakkarpress.platform.dto.request.RegisterRequest;
import com.drakkarpress.platform.dto.response.AuthResponse;
import com.drakkarpress.platform.model.*;
import com.drakkarpress.platform.repository.*;
import com.drakkarpress.platform.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private RuneRepository runeRepository;

    @Autowired
    private UserRuneRepository userRuneRepository;

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private EmailServiceBase emailService;

    /**
     * Registro de nuevo usuario
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que no exista email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Validar que no exista username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Obtener siguiente user_number
        Long maxUserNumber = userRepository.findMaxUserNumber().orElse(0L);
        Long newUserNumber = maxUserNumber + 1;

        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getDisplayName());
        user.setUserNumber(newUserNumber);
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Calcular precio según fase (automático)
        PricingService.PricingInfo pricing = pricingService.calculatePricing(newUserNumber);

        // Crear membresía FREE con información de fase
        Membership membership = new Membership();
        membership.setUser(user);
        membership.setPlan("FREE");
        membership.setStatus("ACTIVE");
        membership.setPricePaid(BigDecimal.ZERO);
        membership.setIsActive(true);
        membership.setIsGrandfathered(pricing.isGrandfathered);
        membership.setIsCourtesy(false);
        membership.setStartedAt(LocalDateTime.now());
        
        // Guardar información de fase para futuro upgrade
        membership.setPaymentFrequency("ANNUAL");
        
        membershipRepository.save(membership);

        // Asignar runa por defecto (Fehu - primera runa) - opcional, no bloquear registro
        try {
            Rune defaultRune = runeRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .orElse(null);
            
            if (defaultRune != null) {
                UserRune userRune = new UserRune();
                userRune.setUser(user);
                userRune.setRune(defaultRune);
                userRune.setIsActive(true);
                userRune.setSelectedAt(LocalDateTime.now());
                
                userRuneRepository.save(userRune);
            } else {
                System.out.println("[AUTH] ⚠️  Default rune not found, skipping rune assignment");
            }
        } catch (Exception e) {
            System.err.println("[AUTH] ⚠️  Error assigning default rune (non-blocking): " + e.getMessage());
        }

        // Generar tokens
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        // Guardar sesión
        createSession(user.getId(), accessToken, refreshToken);

        // Enviar email de bienvenida (async)
        try {
            System.out.println("[AUTH] 📨 Intentando enviar welcome email para: " + user.getEmail());
            emailService.sendWelcomeEmail(user, pricing);
            System.out.println("[AUTH] ✅ Welcome email dispatch completado");
        } catch (Exception e) {
            // Ignorar errores de email para permitir registro sin SMTP configurado
            System.err.println("[AUTH] ⚠️  Email no enviado (SMTP no configurado o error): " + e.getMessage());
            e.printStackTrace();
        }

        return new AuthResponse(
                accessToken,
                refreshToken,
                900L, // 15 minutos en segundos
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getUserNumber().intValue(),
                "FREE"
        );
    }

    /**
     * Login de usuario
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrUsername(),
                        request.getPassword()
                )
        );

        // Buscar usuario
        User user = userRepository.findByEmailOrUsername(request.getEmailOrUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Buscar membresía
        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        // Generar tokens
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        // Guardar sesión
        createSession(user.getId(), accessToken, refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                900L,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getUserNumber().intValue(),
                membership != null ? membership.getPlan() : "FREE"
        );
    }

    /**
     * Refresh token
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validar refresh token
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token type");
        }

        // Buscar sesión
        SessionToken session = sessionTokenRepository.findByRefreshTokenHash(hashToken(refreshToken))
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getIsActive() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Session expired or inactive");
        }

        // Obtener user
        User user = session.getUser();

        // Obtener membership
        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        // Generar nuevos tokens (rotación de refresh)
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole(), user.getSubscription());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());
        String jti = tokenProvider.getJtiFromToken(newAccessToken);

        // Actualizar sesión con nuevo refresh hash y expiraciones
        session.setAccessTokenJti(jti);
        session.setRefreshTokenHash(hashToken(newRefreshToken));
        LocalDateTime refreshExpiry = tokenProvider.getExpirationDateFromToken(newRefreshToken).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        session.setExpiresAt(refreshExpiry);
        session.setLastUsedAt(LocalDateTime.now());
        sessionTokenRepository.save(session);

        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            900L,
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getFullName(),
            user.getUserNumber().intValue(),
            membership != null ? membership.getPlan() : "FREE"
        );
    }

    /**
     * Logout
     */
    @Transactional
    public void logout(String accessToken) {
        String jti = tokenProvider.getJtiFromToken(accessToken);
        
        SessionToken session = sessionTokenRepository.findByAccessTokenJti(jti)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setIsActive(false);
        session.setRevokedAt(LocalDateTime.now());
        sessionTokenRepository.save(session);
    }

    /**
     * Crear sesión
     */
    private void createSession(UUID userId, String accessToken, String refreshToken) {
        String jti = tokenProvider.getJtiFromToken(accessToken);
        LocalDateTime accessExpiry = tokenProvider.getExpirationDateFromToken(accessToken).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime refreshExpiry = tokenProvider.getExpirationDateFromToken(refreshToken).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SessionToken session = new SessionToken();
        session.setUser(user);
        session.setAccessTokenJti(jti);
        session.setRefreshTokenHash(hashToken(refreshToken));
        session.setExpiresAt(refreshExpiry);
        session.setIsActive(true);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastUsedAt(LocalDateTime.now());

        sessionTokenRepository.save(session);
    }

    /**
     * Inicializar sesión para un flujo de autenticación (registro/login/social).
     * Expone la lógica interna sin duplicar implementación.
     */
    public void initializeSession(UUID userId, String accessToken, String refreshToken) {
        createSession(userId, accessToken, refreshToken);
    }

    /**
     * Hash simple para refresh token (SHA-256)
     */
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    /**
     * Obtener información del usuario actual
     */
    public Object getCurrentUser(String accessToken) {
        try {
            // Validar token y obtener userId
            UUID userId = tokenProvider.getUserIdFromToken(accessToken);
            
            // Buscar usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Obtener membresías del usuario (podría tener varias)
            var memberships = membershipRepository.findByUserId(user.getId());
            Membership activeMembership = memberships.stream()
                    .filter(m -> "ACTIVE".equals(m.getStatus()))
                    .findFirst()
                    .orElse(null);
            
            // Construir respuesta con datos del usuario
            return new java.util.HashMap<String, Object>() {{
                put("id", user.getId());
                put("email", user.getEmail());
                put("username", user.getUsername());
                put("fullName", user.getFullName());
                put("userNumber", user.getUserNumber());
                put("country", user.getCountry());
                put("bio", user.getBio());
                put("profilePictureUrl", user.getProfilePictureUrl());
                put("languagePreference", user.getLanguagePreference());
                put("isEmailVerified", user.getIsEmailVerified());
                put("membership", activeMembership != null ? new java.util.HashMap<String, Object>() {{
                    put("plan", activeMembership.getPlan());
                    put("status", activeMembership.getStatus());
                    put("createdAt", activeMembership.getCreatedAt());
                }} : null);
                put("createdAt", user.getCreatedAt());
                put("lastLoginAt", user.getLastLoginAt());
            }};
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info: " + e.getMessage());
        }
    }

    /**
     * Health check
     */
    public Object health() {
        return new java.util.HashMap<String, Object>() {{
            put("status", "UP");
            put("timestamp", java.time.LocalDateTime.now());
        }};
    }
}
