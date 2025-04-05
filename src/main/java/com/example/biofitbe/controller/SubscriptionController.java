package com.example.biofitbe.controller;

import com.example.biofitbe.dto.SubscriptionDTO;
import com.example.biofitbe.model.Subscription;
import com.example.biofitbe.repository.SubscriptionRepository;
import com.example.biofitbe.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/status/{userId}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionStatus(@PathVariable Long userId) {
        Optional<Subscription> optSubscription = subscriptionRepository.findByUserIdAndIsActiveTrue(userId);

        if (optSubscription.isPresent()) {
            Subscription subscription = optSubscription.get();

            // Kiểm tra xem đăng ký đã hết hạn chưa
            if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
                subscription.setActive(false);
                subscriptionRepository.save(subscription);
                return ResponseEntity.ok(convertToDTO(subscription));
            }

            return ResponseEntity.ok(convertToDTO(subscription));
        } else {
            // Trả lại đăng ký không hoạt động
            SubscriptionDTO dto = new SubscriptionDTO();
            dto.setUserId(userId);
            dto.setActive(false);
            return ResponseEntity.ok(dto);
        }
    }

    private SubscriptionDTO convertToDTO(Subscription subscription) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUserId());
        dto.setPlanType(subscription.getPlanType());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setActive(subscription.isActive());
        return dto;
    }

    // kiểm tra trạng thái đăng kí gói nếu đã hết hạn
    public ResponseEntity<Boolean> checkSubscription(@PathVariable long userId) {
        boolean isExpired = subscriptionService.isSubscriptionExpired(userId);
        return ResponseEntity.ok(isExpired);
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<SubscriptionDTO> getLatestSubscription(@PathVariable Long userId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findLatestSubscription(userId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            SubscriptionDTO subscriptionDTO = new SubscriptionDTO(
                    subscription.getId(),
                    subscription.getUserId(),
                    subscription.getPlanType(),
                    subscription.getStartDate(),
                    subscription.getEndDate(),
                    subscription.isActive(),
                    subscription.getTotalSubscriptionDays()
            );
            return ResponseEntity.ok(subscriptionDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}