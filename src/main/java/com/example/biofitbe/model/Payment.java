package com.example.biofitbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String orderId;
    private BigDecimal amount;
    private String planType;
    private String paymentMethod;
    private String paymentStatus; // PENDING, COMPLETED, FAILED, CANCELED

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
