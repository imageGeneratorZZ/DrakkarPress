package com.drakkarpress.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // segundos
    private UUID userId;
    private String email;
    private String username;
    private String displayName;
    private Integer userNumber;
    private String membershipPlan;
    
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UUID userId, 
                       String email, String username, String displayName, Integer userNumber, String membershipPlan) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.userNumber = userNumber;
        this.membershipPlan = membershipPlan;
    }
}
