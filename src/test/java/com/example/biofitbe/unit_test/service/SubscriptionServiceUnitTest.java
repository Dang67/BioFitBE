package com.example.biofitbe.unit_test.service;

import com.example.biofitbe.model.Subscription;
import com.example.biofitbe.repository.SubscriptionRepository;
import com.example.biofitbe.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUnitTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testIsSubscriptionExpired_NoSubscription_ShouldReturnTrue() {
        Long userId = 1L;

        // Giả lập không có subscription
        Mockito.when(subscriptionRepository.findLatestSubscription(userId))
                .thenReturn(Optional.empty());

        boolean result = subscriptionService.isSubscriptionExpired(userId);

        Assertions.assertTrue(result, "Người dùng không có gói suy ra phải hết hạn");
    }

    @Test
    public void testIsSubscriptionExpired_SubscriptionExpired_ShouldReturnTrue() {
        Long userId = 2L;
        Subscription subscription = new Subscription();
        subscription.setEndDate(LocalDateTime.now().minusDays(1)); // Đã hết hạn

        Mockito.when(subscriptionRepository.findLatestSubscription(userId))
                .thenReturn(Optional.of(subscription));

        boolean result = subscriptionService.isSubscriptionExpired(userId);

        Assertions.assertTrue(result, "Gói đã hết hạn suy ra phải trả về true");
    }

    @Test
    public void testIsSubscriptionExpired_SubscriptionActive_ShouldReturnFalse() {
        Long userId = 3L;
        Subscription subscription = new Subscription();
        subscription.setEndDate(LocalDateTime.now().plusDays(5)); // Còn hạn

        Mockito.when(subscriptionRepository.findLatestSubscription(userId))
                .thenReturn(Optional.of(subscription));

        boolean result = subscriptionService.isSubscriptionExpired(userId);

        Assertions.assertFalse(result, "Gói còn hạn suy ra phải trả về false");
    }
}