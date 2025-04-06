package com.example.biofitbe.repository;

import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class SubscriptionRepositoryIntegrationTest {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setup() {
        subscriptionRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    void findByUserIdAndIsActiveTrue_ShouldReturnActiveSubscription_WhenExists() {

        Long userId = 1L;

        // Tạo và lưu Payment
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(100.0));
        paymentRepository.save(payment); // Lưu Payment trước

        // Chỉ tạo một Subscription duy nhất cho userId = 1
        Subscription activeSubscription = new Subscription();
        activeSubscription.setUserId(userId);
        activeSubscription.setActive(true);
        activeSubscription.setStartDate(LocalDateTime.now().minusDays(5));
        activeSubscription.setEndDate(LocalDateTime.now().plusDays(25));
        activeSubscription.setPlanType("MONTHLY");
        activeSubscription.setPayment(payment); // Gán Payment đã lưu
        subscriptionRepository.save(activeSubscription);

        Optional<Subscription> result = subscriptionRepository.findByUserIdAndIsActiveTrue(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertTrue(result.get().isActive());
        assertEquals("MONTHLY", result.get().getPlanType());
    }

    @Test
    void findByUserIdAndIsActiveTrue_ShouldReturnEmpty_WhenNoActiveSubscription() {

        Long userId = 2L;

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(100.0));
        paymentRepository.save(payment);

        Subscription inactiveSubscription = new Subscription();
        inactiveSubscription.setUserId(userId);
        inactiveSubscription.setActive(false);
        inactiveSubscription.setStartDate(LocalDateTime.now().minusDays(35));
        inactiveSubscription.setEndDate(LocalDateTime.now().minusDays(5));
        inactiveSubscription.setPlanType("MONTHLY");
        inactiveSubscription.setPayment(payment);
        subscriptionRepository.save(inactiveSubscription);

        Optional<Subscription> result = subscriptionRepository.findByUserIdAndIsActiveTrue(userId);
        assertFalse(result.isPresent());
    }

    @Test
    void findLatestSubscription_ShouldReturnMostRecentSubscription_WhenMultipleExist() {

        Long userId = 6L;

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(100.0));
        paymentRepository.save(payment);


        Subscription newerSubscription = new Subscription();
        newerSubscription.setUserId(userId);
        newerSubscription.setStartDate(LocalDateTime.now().minusDays(20));
        newerSubscription.setEndDate(LocalDateTime.now().plusDays(10));
        newerSubscription.setPlanType("YEARLY");
        newerSubscription.setPayment(payment); // Gán Payment đã lưu
        newerSubscription.setActive(true);
        subscriptionRepository.save(newerSubscription);


        Optional<Subscription> result = subscriptionRepository.findLatestSubscription(userId);
        assertTrue(result.isPresent());
        assertEquals(newerSubscription.getEndDate(), result.get().getEndDate());
        assertEquals("YEARLY", result.get().getPlanType());
    }

    @Test
    void findLatestSubscription_ShouldReturnEmpty_WhenNoSubscriptionsExist() {

        Long userId = 4L;

        Optional<Subscription> result = subscriptionRepository.findLatestSubscription(userId);
        assertFalse(result.isPresent());
    }
}
