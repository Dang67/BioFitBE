package com.example.biofitbe.service;

import com.example.biofitbe.model.Subscription;
import com.example.biofitbe.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public boolean isSubscriptionExpired(Long userId) {
        Optional<Subscription> optSubscription = subscriptionRepository.findLatestSubscription(userId);
        if (optSubscription.isEmpty()) {
            return true; // Không có gói nào, xem như hết hạn
        }

        Subscription subscription = optSubscription.get();
        return subscription.getEndDate().isBefore(LocalDateTime.now());
    }
}