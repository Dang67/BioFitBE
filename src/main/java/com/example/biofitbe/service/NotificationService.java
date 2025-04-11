package com.example.biofitbe.service;

import com.example.biofitbe.dto.NotificationDTO;
import com.example.biofitbe.dto.NotificationRequest;
import com.example.biofitbe.model.Notification;
import com.example.biofitbe.repository.NotificationRepository;
import com.example.biofitbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private void scheduleSleepNotification(String userId) {
        LocalDateTime today = LocalDateTime.now().with(LocalTime.of(22, 0));

        // Thông báo chính
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Đã đến giờ đi ngủ rôi 🍳")
                .message("Đi ngủ để mai có một năng lượng dồi dào cho ngày mới nhé!")
                .mealType(Notification.MealType.BREAKFAST)
                .scheduledTime(today)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(notification);

        // Thông báo nhắc nhở sau 5 phút
        Notification reminder = Notification.builder()
                .userId(userId)
                .title("Nhắc nhở ăn sáng 🥞")
                .message("Đừng quên ăn sáng để có năng lượng cho buổi sáng nhé!")
                .mealType(Notification.MealType.BREAKFAST)
                .scheduledTime(today.plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(reminder);
    }

    private void scheduleBreakfastNotifications(String userId) {
        LocalDateTime today = LocalDateTime.now().with(LocalTime.of(7, 0));

        // Thông báo chính
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Đã đến giờ ăn sáng rồi! 🍳")
                .message("Chào buổi sáng! Hãy nạp năng lượng cho ngày mới nhé!")
                .mealType(Notification.MealType.BREAKFAST)
                .scheduledTime(today)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(notification);

        // Thông báo nhắc nhở sau 5 phút
        Notification reminder = Notification.builder()
                .userId(userId)
                .title("Nhắc nhở ăn sáng 🥞")
                .message("Đừng quên ăn sáng để có năng lượng cho buổi sáng nhé!")
                .mealType(Notification.MealType.BREAKFAST)
                .scheduledTime(today.plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(reminder);
    }

    private void scheduleLunchNotifications(String userId) {
        LocalDateTime today = LocalDateTime.now().with(LocalTime.of(12, 0));

        // Thông báo chính
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Đã đến giờ ăn trưa rồi! 🍲")
                .message("Hãy dành thời gian để thưởng thức bữa trưa dinh dưỡng nhé!")
                .mealType(Notification.MealType.LUNCH)
                .scheduledTime(today)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(notification);

        // Thông báo nhắc nhở sau 10 phút
        Notification reminder = Notification.builder()
                .userId(userId)
                .title("Nhắc nhở ăn trưa 🥗")
                .message("Đừng bỏ bữa trưa! Hãy nạp năng lượng để hoàn thành tốt công việc buổi chiều.")
                .mealType(Notification.MealType.LUNCH)
                .scheduledTime(today.plusMinutes(10))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(reminder);
    }

    private void scheduleDinnerNotifications(String userId) {
        LocalDateTime today = LocalDateTime.now().with(LocalTime.of(18, 30));

        // Thông báo chính
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Đã đến giờ ăn tối rồi! 🍽️")
                .message("Hãy thưởng thức bữa tối và thư giãn sau một ngày dài nhé!")
                .mealType(Notification.MealType.DINNER)
                .scheduledTime(today)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(notification);

        // Thông báo nhắc nhở sau 10 phút
        Notification reminder = Notification.builder()
                .userId(userId)
                .title("Nhắc nhở ăn tối 🍚")
                .message("Đừng quên ăn tối đầy đủ dinh dưỡng để kết thúc ngày một cách lành mạnh.")
                .mealType(Notification.MealType.DINNER)
                .scheduledTime(today.plusMinutes(10))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(reminder);
    }

    private void scheduleSnackNotifications(String userId) {
        // Snack buổi sáng
        LocalDateTime morningSnack = LocalDateTime.now().with(LocalTime.of(10, 0));
        Notification morningSnackNotification = Notification.builder()
                .userId(userId)
                .title("Giờ ăn nhẹ buổi sáng 🍎")
                .message("Một chút hoa quả hoặc hạt sẽ giúp bạn duy trì năng lượng đến bữa trưa!")
                .mealType(Notification.MealType.SNACK)
                .scheduledTime(morningSnack)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(morningSnackNotification);

        // Snack buổi chiều
        LocalDateTime afternoonSnack = LocalDateTime.now().with(LocalTime.of(15, 0));
        Notification afternoonSnackNotification = Notification.builder()
                .userId(userId)
                .title("Giờ ăn nhẹ buổi chiều 🥜")
                .message("Hãy nạp chút năng lượng để hoàn thành tốt công việc cuối ngày!")
                .mealType(Notification.MealType.SNACK)
                .scheduledTime(afternoonSnack)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        notificationRepository.save(afternoonSnackNotification);
    }

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByScheduledTimeDesc(userId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    // Thêm phương thức xóa thông báo
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Thêm phương thức tạo thông báo
    public Notification createNotification(NotificationRequest request, String userId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(request.getTitle())
                .message(request.getMessage())
                .mealType(Notification.MealType.valueOf(request.getMealType()))
                .scheduledTime(request.getScheduledTime())
                .createdAt(LocalDateTime.now())
                .isRead(request.isRead())
                .isReminderSent(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 0 7,12,18,22 * * ?") // 7h, 12h, 18h, 21h hàng ngày
    public void sendScheduledMealNotifications() {
        LocalDateTime now = LocalDateTime.now();

        // Lấy tất cả user cần nhận thông báo
        List<Long> allUserIds = userRepository.findAllUserIds(); // Giả sử có repository này

        allUserIds.forEach(userId -> {
            if (now.getHour() == 7) {
                scheduleBreakfastNotifications(String.valueOf(userId));
            } else if (now.getHour() == 12) {
                scheduleLunchNotifications(String.valueOf(userId));
            } else if (now.getHour() == 18) {
                scheduleDinnerNotifications(String.valueOf(userId));
            } else if (now.getHour() == 22) {
                scheduleSleepNotification(String.valueOf(userId));
            }
        });
    }

    public Notification createWelcomeNotification(String userId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Chào mừng đến với BioFit!")
                .message("Cảm ơn bạn đã sử dụng ứng dụng. Hãy bắt đầu hành trình sức khỏe của bạn!")
                .mealType(Notification.MealType.OTHER)
                .scheduledTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isReminderSent(false)
                .build();

        return notificationRepository.save(notification);
    }
}
