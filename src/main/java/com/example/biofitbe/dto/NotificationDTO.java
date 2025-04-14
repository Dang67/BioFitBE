package com.example.biofitbe.dto;

import com.example.biofitbe.model.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String mealType;
    private LocalDateTime scheduledTime;
    private boolean isRead;

    public static NotificationDTO fromEntity(Notification entity) {
        return NotificationDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .mealType(entity.getMealType().name())
                .scheduledTime(entity.getScheduledTime())
                .isRead(entity.isRead())
                .build();
    }
}
