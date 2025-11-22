package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.dto.AdminGrantMembershipRequest;
import com.drakkarpress.platform.dto.AdminGrantMembershipResponse;
import com.drakkarpress.platform.dto.ApiResponse;
import com.drakkarpress.platform.model.Membership;
import com.drakkarpress.platform.model.User;
import com.drakkarpress.platform.repository.MembershipRepository;
import com.drakkarpress.platform.repository.PlatformUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/memberships")
public class AdminMembershipController {

    private final PlatformUserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public AdminMembershipController(PlatformUserRepository userRepository, MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @PostMapping("/grant")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<AdminGrantMembershipResponse>> grantMembership(@RequestBody AdminGrantMembershipRequest request) {
        if (request.getUserId() == null || request.getPlan() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("userId y plan son requeridos"));
        }
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Usuario no encontrado"));
        }
        User user = userOpt.get();

        Membership membership = membershipRepository.findByUserId(user.getId()).orElseGet(() -> {
            Membership m = new Membership();
            m.setUser(user);
            m.setPlan("FREE");
            m.setStatus("ACTIVE");
            m.setStartedAt(LocalDateTime.now());
            m.setIsActive(true);
            m.setIsCourtesy(false);
            m.setIsGrandfathered(false);
            return m;
        });

        String plan = request.getPlan().trim().toUpperCase();
        boolean courtesy = plan.equals("PREMIUM_COURTESY");
        boolean grandfathered = plan.startsWith("PREMIUM_PHASE_");
        String frequency = request.getBillingCycle() == null ? "MONTHLY" : request.getBillingCycle().trim().toUpperCase();
        if (!(frequency.equals("MONTHLY") || frequency.equals("ANNUAL"))) {
            return ResponseEntity.badRequest().body(ApiResponse.error("billingCycle inválido (MONTHLY|ANNUAL)"));
        }

        // Calcular precio (BigDecimal) según plan/fase
        BigDecimal price;
        if (courtesy) {
            price = BigDecimal.ZERO;
        } else if (plan.equals("PREMIUM_PHASE_1")) {
            price = frequency.equals("MONTHLY") ? new BigDecimal("5.00") : new BigDecimal("50.00");
        } else if (plan.equals("PREMIUM_PHASE_2")) {
            price = frequency.equals("MONTHLY") ? new BigDecimal("10.00") : new BigDecimal("100.00");
        } else if (plan.equals("PREMIUM_PHASE_3")) {
            price = frequency.equals("MONTHLY") ? new BigDecimal("15.00") : new BigDecimal("150.00");
        } else if (plan.equals("PREMIUM_REGULAR")) {
            price = frequency.equals("MONTHLY") ? new BigDecimal("19.90") : new BigDecimal("170.00");
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Plan inválido"));
        }

        membership.setPlan(plan);
        membership.setStatus("ACTIVE");
        membership.setPaymentFrequency(frequency);
        membership.setPricePaid(price);
        membership.setIsCourtesy(courtesy);
        membership.setIsGrandfathered(grandfathered);
        membership.setIsActive(true);
        if (courtesy && request.getCourtesyReason() != null) {
            membership.setCourtesyReason(request.getCourtesyReason().trim());
        }
        if (membership.getStartedAt() == null) {
            membership.setStartedAt(LocalDateTime.now());
        }
        membershipRepository.save(membership);

        AdminGrantMembershipResponse resp = new AdminGrantMembershipResponse();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setPlan(plan);
        resp.setCourtesy(courtesy);
        resp.setGrandfathered(grandfathered);
        resp.setPaymentFrequency(frequency);
        resp.setStatus("ACTIVE");
        resp.setPricePaid(price.toPlainString());
        resp.setGrantedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success("Membresía actualizada", resp));
    }
}
