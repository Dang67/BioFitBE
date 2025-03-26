package com.example.biofitbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String message;
    private String paymentUrl; // URL to redirect user to payment gateway
    private String orderId;
}
