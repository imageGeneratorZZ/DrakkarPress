package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.BookPurchase;
import com.drakkarpress.platform.model.PurchaseDedication;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.PurchaseDedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DedicationService {

    private final PurchaseDedicationRepository dedicationRepository;
    private static final int MAX_LEN = 500;
    private static final Set<String> BLOCKED = Set.of("puta","mierda","racista","odio","violencia");

    @Transactional
    public PurchaseDedication createDedication(BookPurchase purchase, User user, String message) {
        String raw = message == null ? "" : message.trim();
        String sanitized = sanitize(raw);
        String hash = sha256(purchase.getId() + ":" + user.getId() + ":" + sanitized + ":" + System.currentTimeMillis());
        PurchaseDedication dedication = PurchaseDedication.builder()
                .purchase(purchase)
                .user(user)
                .rawMessage(raw.isBlank() ? null : raw)
                .sanitizedMessage(sanitized)
                .hash(hash)
                .injected(false)
                .epubPath(purchase.getFilePath())
                .build();
        return dedicationRepository.save(dedication);
    }

    public PurchaseDedication markInjected(PurchaseDedication d, String newPath) {
        d.setInjected(true);
        d.setEpubPath(newPath);
        return dedicationRepository.save(d);
    }

    public PurchaseDedication verify(String hash) {
        return dedicationRepository.findByHash(hash).orElse(null);
    }

    private String sanitize(String in) {
        if (in == null) return null;
        String truncated = in.length() > MAX_LEN ? in.substring(0, MAX_LEN) : in;
        String lowered = truncated.toLowerCase();
        for (String bad : BLOCKED) {
            lowered = lowered.replace(bad, "****");
        }
        return lowered;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
