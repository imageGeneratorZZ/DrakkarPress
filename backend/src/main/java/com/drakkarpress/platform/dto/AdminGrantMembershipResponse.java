package com.drakkarpress.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminGrantMembershipResponse {
    private UUID userId;
    private String username;
    private String plan;
    private boolean courtesy;
    private boolean grandfathered;
    private String paymentFrequency;
    private String status;
    private String pricePaid; // texto representando valor (ej: "5.00" o "0")
    private LocalDateTime grantedAt;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public boolean isCourtesy() { return courtesy; }
    public void setCourtesy(boolean courtesy) { this.courtesy = courtesy; }
    public boolean isGrandfathered() { return grandfathered; }
    public void setGrandfathered(boolean grandfathered) { this.grandfathered = grandfathered; }
    public String getPaymentFrequency() { return paymentFrequency; }
    public void setPaymentFrequency(String paymentFrequency) { this.paymentFrequency = paymentFrequency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPricePaid() { return pricePaid; }
    public void setPricePaid(String pricePaid) { this.pricePaid = pricePaid; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
}
