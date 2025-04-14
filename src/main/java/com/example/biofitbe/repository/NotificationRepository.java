package com.example.biofitbe.repository;

import com.example.biofitbe.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByScheduledTimeDesc(String userId);

    @Transactional
    void deleteByUserId(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.mealType = :mealType AND DATE(n.scheduledTime) = :date")
    long countByUserIdAndMealTypeAndDate(@Param("userId") String userId,
                                         @Param("mealType") Notification.MealType mealType,
                                         @Param("date") LocalDateTime date);

}
