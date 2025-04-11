package com.example.biofitbe.repository;

import com.example.biofitbe.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByScheduledTimeDesc(String userId);
//
//    List<Notification> findByUserIdAndIsReadFalseOrderByScheduledTimeDesc(String userId);
//
//    List<Notification> findByScheduledTimeBetweenAndIsReminderSentFalse(
//            LocalDateTime start, LocalDateTime end);
}
