package com.example.biofitbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String title;
    private String message;
    private String mealType;
    private LocalDateTime scheduledTime;
    private boolean isRead;
}

