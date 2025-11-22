package com.drakkarpress.platform.dto;

import java.util.UUID;

public class AdminGrantMembershipRequest {
    private UUID userId; // UUID del usuario
    private String plan; // PREMIUM_PHASE_1, PREMIUM_PHASE_2, PREMIUM_PHASE_3, PREMIUM_REGULAR, PREMIUM_COURTESY
    private String billingCycle; // MONTHLY o ANNUAL
    private String courtesyReason; // opcional si cortesía

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public String getCourtesyReason() { return courtesyReason; }
    public void setCourtesyReason(String courtesyReason) { this.courtesyReason = courtesyReason; }
}
