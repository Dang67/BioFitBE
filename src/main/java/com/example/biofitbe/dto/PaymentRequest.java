package com.example.biofitbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String planType; // "MONTHLY" or "YEARLY"
    private String paymentMethod; // "VNPAY" or "MOMO"
    private String returnUrl; // URL to return after payment
    private String ipAddress; // User's IP address
    private Long userId; // User's ID
}
