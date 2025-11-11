package com.drakkarpress.backend.dto.lulu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuluPrintJobDTO {
    private String orderId; // ID de Lulu
    private String status; // CREATED, PROCESSING, SHIPPED, DELIVERED
    private String trackingNumber;
    private String trackingUrl;
    private String estimatedDeliveryDate;
    
    private Long userId;
    private Long bookId;
    private Integer quantity;
    
    private ShippingAddressDTO shippingAddress;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressDTO {
        private String name;
        private String street1;
        private String street2;
        private String city;
        private String stateCode;
        private String postalCode;
        private String countryCode;
        private String phoneNumber;
    }
}
