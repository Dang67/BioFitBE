package com.example.biofitbe.repository;

import com.example.biofitbe.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // tìm subscription đang hoạt động của một người dùng
    Optional<Subscription> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId ORDER BY s.endDate DESC LIMIT 1")
    Optional<Subscription> findLatestSubscription(@Param("userId") long userId);
}