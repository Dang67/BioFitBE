package com.example.biofitbe.integration_test.repository;

import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    // Test phương thức save và findById
    @Test
    void testSaveAndFindById() {
        // Arrange: Tạo một user mới
        User user = User.builder()
                .fullName("BioFit Test User")
                .email("biofittest@example.com")
                .hashPassword("encodedPassword")
                .gender(1)
                .height(175.5f)
                .weight(70.0f)
                .targetWeight(65.0f)
                .dateOfBirth("1990-01-01")
                .avatar(new byte[]{1, 2, 3}) // Giả lập mảng byte cho avatar
                .createdAccount("2023-01-01")
                .build();

        // Act: Lưu user vào cơ sở dữ liệu
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getUserId();

        // Assert: Kiểm tra user đã được lưu và tìm lại bằng ID
        Optional<User> foundUser = userRepository.findById(userId);
        assertTrue(foundUser.isPresent());
        User result = foundUser.get();
        assertEquals("BioFit Test User", result.getFullName());
        assertEquals("biofittest@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getHashPassword());
        assertEquals(1, result.getGender());
        assertEquals(175.5f, result.getHeight());
        assertEquals(70.0f, result.getWeight());
        assertEquals(65.0f, result.getTargetWeight());
        assertEquals("1990-01-01", result.getDateOfBirth());
        assertArrayEquals(new byte[]{1, 2, 3}, result.getAvatar());
        assertEquals("2023-01-01", result.getCreatedAccount());
        assertEquals("123456", result.getResetCode());
        assertNotNull(result.getResetCodeExpiry());
    }

    // Test phương thức findByEmail
    @Test
    void testFindByEmail() {
        // Arrange: Tạo và lưu một user
        User user = User.builder()
                .email("biofittest@example.com")
                .hashPassword("encodedPassword")
                .createdAccount("2023-01-01")
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // Act: Tìm user bằng email
        Optional<User> foundUser = userRepository.findByEmail("biofittest@example.com");

        // Assert: Kiểm tra kết quả
        assertTrue(foundUser.isPresent());
        assertEquals("biofittest@example.com", foundUser.get().getEmail());
        assertEquals("encodedPassword", foundUser.get().getHashPassword());

        // Trường hợp không tìm thấy
        Optional<User> notFoundUser = userRepository.findByEmail("biofitunknown@example.com");
        assertFalse(notFoundUser.isPresent());
    }

    // Test phương thức findByUserId
    @Test
    void testFindByUserId() {
        // Arrange: Tạo và lưu một user
        User user = User.builder()
                .email("biofittest@example.com")
                .hashPassword("encodedPassword")
                .createdAccount("2023-01-01")
                .build();
        User savedUser = entityManager.persist(user);
        entityManager.flush();
        Long userId = savedUser.getUserId();

        // Act: Tìm user bằng userId
        Optional<User> foundUser = userRepository.findByUserId(userId);

        // Assert: Kiểm tra kết quả
        assertTrue(foundUser.isPresent());
        assertEquals("biofittest@example.com", foundUser.get().getEmail());
        assertEquals("encodedPassword", foundUser.get().getHashPassword());

        // Trường hợp không tìm thấy
        Optional<User> notFoundUser = userRepository.findByUserId(999L);
        assertFalse(notFoundUser.isPresent());
    }

    // Test phương thức findAll
    @Test
    void testFindAll() {
        // Arrange: Tạo và lưu hai user
        User user1 = User.builder()
                .email("biofituser1@example.com")
                .hashPassword("pass1")
                .createdAccount("2023-01-01")
                .build();
        entityManager.persist(user1);

        User user2 = User.builder()
                .email("biofituser2@example.com")
                .hashPassword("pass2")
                .createdAccount("2023-01-02")
                .build();
        entityManager.persist(user2);

        entityManager.flush();

        // Act: Lấy tất cả user
        List<User> users = userRepository.findAll();

        // Assert: Kiểm tra danh sách
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("biofituser1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("biofituser2@example.com")));
    }
}