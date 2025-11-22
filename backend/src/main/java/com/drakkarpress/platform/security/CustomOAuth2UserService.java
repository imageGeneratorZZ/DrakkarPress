package com.drakkarpress.platform.security;

import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.Rune;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.model.UserRune;
import com.drakkarpress.platform.repository.MembershipRepository;
import com.drakkarpress.platform.repository.RuneRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import com.drakkarpress.platform.repository.UserRuneRepository;
import com.drakkarpress.platform.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.drakkarpress.platform.metrics.SocialProvisioningMetrics;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * CustomOAuth2UserService
 * - Provisiona usuarios sociales (Google / Facebook)
 * - Crea usuario FREE inicial + membresía + runa por defecto
 * - Marca email verificado automáticamente
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private PlatformUserRepository userRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private RuneRepository runeRepository;
    @Autowired
    private UserRuneRepository userRuneRepository;
    @Autowired
    private PricingService pricingService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SocialProvisioningMetrics metrics;

    private static final UUID DEFAULT_RUNE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Pattern USERNAME_SAFE = Pattern.compile("[^a-zA-Z0-9_]");

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User delegate = super.loadUser(userRequest);
        Map<String, Object> attrs = delegate.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Extraer datos básicos
        String email = getEmail(attrs, registrationId);
        String name = getName(attrs, registrationId);
        String picture = getPicture(attrs, registrationId);

        // Provisionar si no existe
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            metrics.incrementNew();
            return provisionUser(email, name, picture);
        });
        if (user != null) {
            metrics.incrementExisting(); // cuenta acceso (nuevo ya sumado arriba; existente sólo este)
        }

        // user-name-attribute (sub/id) para DefaultOAuth2User
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (userNameAttributeName == null || userNameAttributeName.isBlank()) {
            userNameAttributeName = "email"; // fallback
        }

        return new DefaultOAuth2User(delegate.getAuthorities(), attrs, userNameAttributeName);
    }

    private User provisionUser(String email, String name, String pictureUrl) {
        Long maxUserNumber = userRepository.findMaxUserNumber().orElse(0L);
        Long newUserNumber = maxUserNumber + 1;

        String baseUsername = deriveUsernameFromEmailOrName(email, name);
        String finalUsername = ensureUniqueUsername(baseUsername);

        User user = new User();
        user.setEmail(email);
        user.setUsername(finalUsername);
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID() + "!OAUTH2"));
        user.setFullName(name);
        user.setProfilePictureUrl(pictureUrl);
        user.setUserNumber(newUserNumber);
        user.setIsActive(true);
        user.setIsEmailVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // Crear membresía FREE inicial (como en registro normal)
        PricingService.PricingInfo pricing = pricingService.calculatePricing(newUserNumber);
        Membership membership = new Membership();
        membership.setUser(user);
        membership.setPlan("FREE");
        membership.setStatus("ACTIVE");
        membership.setPaymentFrequency("ANNUAL");
        membership.setPricePaid(BigDecimal.ZERO);
        membership.setIsActive(true);
        membership.setIsGrandfathered(pricing.isGrandfathered);
        membership.setIsCourtesy(false);
        membership.setStartedAt(LocalDateTime.now());
        membershipRepository.save(membership);

        // Asignar runa por defecto
        // Recuperar runa por defecto (si existe); ignorar advertencia de null-safety por UUID constante
        Rune defaultRune = runeRepository.findById(DEFAULT_RUNE_ID).orElse(null); // no fallar si no existe todavía
        if (defaultRune != null) {
            UserRune ur = new UserRune();
            ur.setUser(user);
            ur.setRune(defaultRune);
            ur.setIsActive(true);
            ur.setSelectedAt(LocalDateTime.now());
            userRuneRepository.save(ur);
        }

        return user;
    }

    private String deriveUsernameFromEmailOrName(String email, String name) {
        if (email != null && !email.isBlank() && email.contains("@")) {
            String local = email.substring(0, email.indexOf('@'));
            local = USERNAME_SAFE.matcher(local.toLowerCase()).replaceAll("_");
            return trimAndNormalize(local);
        }
        if (name != null && !name.isBlank()) {
            String norm = USERNAME_SAFE.matcher(name.toLowerCase()).replaceAll("_");
            return trimAndNormalize(norm);
        }
        return "user" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    private String trimAndNormalize(String s) {
        s = s.replaceAll("__+", "_");
        if (s.length() > 20) s = s.substring(0, 20);
        if (s.isBlank()) s = "user" + ThreadLocalRandom.current().nextInt(1000, 9999);
        return s;
    }

    private String ensureUniqueUsername(String base) {
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + suffix++;
            if (candidate.length() > 30) {
                candidate = candidate.substring(0, 30);
            }
        }
        return candidate;
    }

    private String getEmail(Map<String, Object> attrs, String provider) {
        Object email = attrs.get("email");
        if (email instanceof String && !((String) email).isBlank()) {
            return (String) email;
        }
        // Fallback para Facebook sin email
        Object id = attrs.get("id");
        return (id != null ? id.toString() : UUID.randomUUID().toString()) + "@" + provider + ".local";
    }

    private String getName(Map<String, Object> attrs, String provider) {
        Object name = attrs.get("name");
        if (name instanceof String && !((String) name).isBlank()) {
            return (String) name;
        }
        Object givenName = attrs.get("given_name");
        Object familyName = attrs.get("family_name");
        if (givenName != null || familyName != null) {
            return ((givenName != null) ? givenName.toString() : "") + " " + ((familyName != null) ? familyName.toString() : "").trim();
        }
        return provider + " User";
    }

    private String getPicture(Map<String, Object> attrs, String provider) {
        Object picture = attrs.get("picture");
        if (picture instanceof String) {
            return (String) picture;
        }
        // Facebook puede devolver nested object { data: { url: ... } }
        Object fbPicture = attrs.get("picture");
        if (fbPicture instanceof Map) {
            Object data = ((Map<?, ?>) fbPicture).get("data");
            if (data instanceof Map) {
                Object url = ((Map<?, ?>) data).get("url");
                if (url instanceof String) {
                    return (String) url;
                }
            }
        }
        return null;
    }
}