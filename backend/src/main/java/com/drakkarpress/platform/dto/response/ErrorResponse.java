package com.drakkarpress.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    
    private int status;
    private String message;
    private String error;
    private Long timestamp;
    
    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }
}
