package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.PurchaseDedication;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.BookPurchaseRepository; // assume exists
import com.drakkarpress.platform.repository.PurchaseDedicationRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository; // assume platform repository
import com.drakkarpress.platform.service.DedicationService;
import com.drakkarpress.platform.service.EpubDedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dedications")
@RequiredArgsConstructor
public class DedicationController {

    private final DedicationService dedicationService;
    private final EpubDedicationService epubDedicationService;
    private final BookPurchaseRepository purchaseRepository;
    private final PurchaseDedicationRepository dedicationRepository;
    private final PlatformUserRepository userRepository;

    @PostMapping("/{purchaseId}")
    public ResponseEntity<?> create(@PathVariable UUID purchaseId,
                                    @RequestBody Map<String, String> body,
                                    Authentication auth) {
        String message = body.getOrDefault("message", "");
        User user = resolveUser(auth);
        BookPurchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null) return ResponseEntity.notFound().build();
        PurchaseDedication dedication = dedicationService.createDedication(purchase, user, message);
        String updatedPath = epubDedicationService.injectDedication(purchase.getFilePath(), dedication);
        dedicationService.markInjected(dedication, updatedPath);
        return ResponseEntity.ok(Map.of(
                "id", dedication.getId(),
                "hash", dedication.getHash(),
                "message", dedication.getEffectiveMessage(),
                "injected", dedication.isInjected()
        ));
    }

    @GetMapping("/verify/{hash}")
    public ResponseEntity<?> verify(@PathVariable String hash) {
        PurchaseDedication d = dedicationService.verify(hash);
        if (d == null) return ResponseEntity.status(404).body(Map.of("valid", false));
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "hash", d.getHash(),
                "purchaseId", d.getPurchase().getId(),
                "createdAt", d.getCreatedAt(),
                "message", d.getEffectiveMessage()
        ));
    }

    private User resolveUser(Authentication auth) {
        if (auth == null) throw new IllegalStateException("No autenticado");
        Object principal = auth.getPrincipal();
        // Simplificado: asumimos username = email
        String username = principal.toString();
        return userRepository.findByEmail(username).orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
