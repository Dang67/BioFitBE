package com.example.biofitbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    private LocalDateTime scheduledTime;

    private LocalDateTime createdAt;

    private boolean isRead;

    private boolean isReminderSent;

    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, OTHER;
    }
}

