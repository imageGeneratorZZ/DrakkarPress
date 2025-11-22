package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final PlatformUserRepository userRepository;

    public ProfileController(PlatformUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> profile(@PathVariable String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.error("Usuario no encontrado"));
        User user = userOpt.get();
        Membership m = user.getMembership();
        Map<String,Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        payload.put("fullName", user.getFullName());
        payload.put("country", user.getCountry());
        payload.put("language", user.getLanguagePreference());
        payload.put("plan", m != null ? m.getPlan() : "FREE");
        payload.put("premium", m != null && m.isPremium());
        payload.put("rolesCount", user.getRoles().size());
        payload.put("createdAt", user.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("OK", payload));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String,Object>>> me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No autenticado"));
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            username = springUser.getUsername();
        } else if (principal instanceof com.drakkarpress.platform.security.JwtUserPrincipal jwtPrincipal) {
            username = jwtPrincipal.username();
        } else {
            username = principal.toString();
        }
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.error("Usuario no encontrado"));
        User user = userOpt.get();
        Membership m = user.getMembership();
        Map<String,Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("username", user.getUsername());
        payload.put("fullName", user.getFullName());
        payload.put("email", user.getEmail());
        payload.put("bio", user.getBio());
        payload.put("profilePictureUrl", user.getProfilePictureUrl());
        payload.put("country", user.getCountry());
        payload.put("language", user.getLanguagePreference());
        payload.put("plan", m != null ? m.getPlan() : "FREE");
        payload.put("subscription", user.getSubscription());
        payload.put("premium", m != null && m.isPremium());
        payload.put("createdAt", user.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.ok("Perfil propio", payload));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateMe(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No autenticado"));
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            username = springUser.getUsername();
        } else if (principal instanceof com.drakkarpress.platform.security.JwtUserPrincipal jwtPrincipal) {
            username = jwtPrincipal.username();
        } else {
            username = principal.toString();
        }
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(ApiResponse.error("Usuario no encontrado"));
        User user = userOpt.get();

        // Actualizaciones permitidas
        if (request.fullName() != null) user.setFullName(trimOrNull(request.fullName()));
        if (request.bio() != null) user.setBio(limit(request.bio(), 2000));
        if (request.profilePictureUrl() != null) user.setProfilePictureUrl(limit(request.profilePictureUrl(), 500));
        if (request.country() != null) user.setCountry(limit(request.country(), 100));
        if (request.language() != null) user.setLanguagePreference(limit(request.language(), 10));

        User persisted = userRepository.save(user);
        if (persisted != null) user = persisted;
        Map<String,Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        payload.put("fullName", user.getFullName());
        payload.put("bio", user.getBio());
        payload.put("profilePictureUrl", user.getProfilePictureUrl());
        payload.put("country", user.getCountry());
        payload.put("language", user.getLanguagePreference());
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", payload));
    }

    private String trimOrNull(String v) { return v == null ? null : v.trim(); }
    private String limit(String v, int max) { return v == null ? null : (v.length() <= max ? v : v.substring(0, max)); }

    public record UpdateProfileRequest(String fullName, String bio, String profilePictureUrl, String country, String language) {}
}
