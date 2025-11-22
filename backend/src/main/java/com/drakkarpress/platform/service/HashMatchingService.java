package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.ContentHash;
import com.drakkarpress.platform.repository.ContentHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.util.Optional;

/**
 * Hash Matching Service para detección de contenido prohibido
 * Integración con PhotoDNA y hash databases (NCMEC, INTERPOL)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HashMatchingService {

    private final ContentHashRepository contentHashRepository;
    private final WebClient.Builder webClientBuilder;

    /**
     * Verifica si un hash de imagen coincide con contenido prohibido
     */
    @Transactional(readOnly = true)
    public boolean checkImageHash(byte[] imageData) {
        try {
            // 1. Calcular MD5 hash
            String md5Hash = calculateMD5(imageData);
            Optional<ContentHash> md5Match = contentHashRepository.findByHashValueAndIsActiveTrue(md5Hash);
            if (md5Match.isPresent()) {
                log.warn("MD5 hash match found for prohibited content: category={}", md5Match.get().getCategory());
                return true;
            }

            // 2. Calcular SHA-256 hash
            String sha256Hash = calculateSHA256(imageData);
            Optional<ContentHash> sha256Match = contentHashRepository.findByHashValueAndIsActiveTrue(sha256Hash);
            if (sha256Match.isPresent()) {
                log.warn("SHA-256 hash match found for prohibited content: category={}", sha256Match.get().getCategory());
                return true;
            }

            // 3. PhotoDNA perceptual hash (llamada a API externa)
            String photoDnaHash = calculatePhotoDnaHash(imageData);
            if (photoDnaHash != null) {
                Optional<ContentHash> photoDnaMatch = contentHashRepository.findByHashValueAndIsActiveTrue(photoDnaHash);
                if (photoDnaMatch.isPresent()) {
                    log.error("PhotoDNA perceptual hash match - CSAM detected! Blocking immediately.");
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            log.error("Error checking image hash", e);
            // Fail-safe: bloquear en caso de error
            return true;
        }
    }

    /**
     * Calcula PhotoDNA hash usando servicio externo (Microsoft Content Moderator)
     */
    private String calculatePhotoDnaHash(byte[] imageData) {
        try {
            // TODO: Integración real con Microsoft Content Moderator API
            // Por ahora retornamos null (no implementado)
            // En producción:
            // 1. POST imageData a endpoint de Content Moderator
            // 2. Recibir PhotoDNA hash
            // 3. Comparar contra base de datos NCMEC/INTERPOL
            log.debug("PhotoDNA hash calculation not yet implemented - requires Azure Content Moderator subscription");
            return null;
        } catch (Exception e) {
            log.error("Error calculating PhotoDNA hash", e);
            return null;
        }
    }

    /**
     * Calcula MD5 hash de contenido
     */
    private String calculateMD5(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(data);
        return bytesToHex(hash);
    }

    /**
     * Calcula SHA-256 hash de contenido
     */
    private String calculateSHA256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data);
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Administración: agregar hash a lista prohibida
     */
    @Transactional
    public void addProhibitedHash(String hashValue, ContentHash.HashType hashType, 
                                   ContentHash.ContentCategory category, String source) {
        ContentHash hash = ContentHash.builder()
                .hashValue(hashValue)
                .hashType(hashType)
                .category(category)
                .source(source)
                .isActive(true)
                .build();
        contentHashRepository.save(hash);
        log.info("Added prohibited hash to database: type={}, category={}", hashType, category);
    }
}
