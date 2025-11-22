package com.drakkarpress.platform.config;

import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.MembershipRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Profile({"h2", "test"})
public class DevDataInitializer {

    private final PlatformUserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdmin() {
        System.out.println("\n[SEED-INIT] DevDataInitializer ejecutándose en perfil h2/test");
        // Create admin if absent
        String adminEmail = "admin@drakkarpress.local";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                Long nextNumber = userRepository.findMaxUserNumber().orElse(0L) + 1;
                User admin = User.builder()
                        .userNumber(nextNumber)
                        .username("admin")
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode("Admin123!"))
                        .enabled(true)
                        .verified(true)
                        .role("ADMIN")
                        .subscription("FREE")
                        .isEmailVerified(true)
                        .isActive(true)
                        .build();
                admin = userRepository.save(java.util.Objects.requireNonNull(admin));

                Membership membership = Membership.builder()
                        .user(admin)
                        .plan("PREMIUM_PHASE_1")
                        .status("ACTIVE")
                        .paymentFrequency("MONTHLY")
                        .pricePaid(new BigDecimal("5.00"))
                        .isCourtesy(false)
                        .isGrandfathered(true)
                        .startedAt(LocalDateTime.now())
                        .isActive(true)
                        .build();
                membershipRepository.save(java.util.Objects.requireNonNull(membership));
                System.out.println("[SEED] Admin creado: admin / Admin123! (PREMIUM_PHASE_1)");
            }
    }
}