package com.example.biofitbe.integration_test.controller;

import com.example.biofitbe.model.Subscription;
import com.example.biofitbe.repository.SubscriptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Long testUserId = 1L;

    @BeforeEach
    public void setup() {
        // Xóa dữ liệu cũ trước mỗi test
        subscriptionRepository.deleteAll();

        // Tạo subscription đang active
        Subscription subscription = new Subscription();
        subscription.setUserId(testUserId);
        subscription.setPlanType("MONTHLY");
        subscription.setStartDate(LocalDateTime.now().minusDays(5));
        subscription.setEndDate(LocalDateTime.now().plusDays(5));
        subscription.setActive(true);

        subscriptionRepository.save(subscription);
    }

    @AfterEach
    void tearDown() {
        subscriptionRepository.deleteAll();
    }

    @Test
    public void testGetSubscriptionStatus_active() throws Exception {
        mockMvc.perform(get("/api/subscription/status/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.planType").value("MONTHLY"));
    }

    @Test
    public void testGetSubscriptionStatus_expired() throws Exception {
        // Tạo subscription đã hết hạn
        Subscription expiredSub = new Subscription();
        expiredSub.setUserId(200L);
        expiredSub.setPlanType("BASIC");
        expiredSub.setStartDate(LocalDateTime.now().minusDays(10));
        expiredSub.setEndDate(LocalDateTime.now().minusDays(1)); // Đã hết hạn
        expiredSub.setActive(true);
        subscriptionRepository.save(expiredSub);

        mockMvc.perform(get("/api/subscription/status/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    public void testGetSubscriptionStatus_notFound() throws Exception {
        mockMvc.perform(get("/api/subscription/status/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(999))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    public void testGetLatestSubscription() throws Exception {
        mockMvc.perform(get("/api/subscription/latest/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    public void testGetLatestSubscription_NotFound() throws Exception {
        mockMvc.perform(get("/api/subscription/latest/9999"))
                .andExpect(status().isNotFound());
    }
}
