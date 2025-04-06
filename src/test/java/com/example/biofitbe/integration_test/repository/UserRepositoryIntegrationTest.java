package com.example.biofitbe.integration_test.repository;

import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // Xóa toàn bộ dữ liệu trước mỗi test để đảm bảo tính độc lập
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById_ShouldReturnSavedUser_WhenSavedSuccessfully() {
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
                .avatar(new byte[]{1, 2, 3})
                .createdAccount("2023-01-01")
                .build();

        // Act: Lưu user và tìm lại bằng ID
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());

        // Assert: Kiểm tra kết quả
        assertTrue(foundUser.isPresent());
        User result = foundUser.get();
        assertEquals("BioFit Test User", result.getFullName());
        assertEquals("biofittest@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getHashPassword());
        assertEquals(1, result.getGender());
        assertEquals(175.5f, result.getHeight(), 0.01f); // Thêm delta cho float comparison
        assertEquals(70.0f, result.getWeight(), 0.01f);
        assertEquals(65.0f, result.getTargetWeight(), 0.01f);
        assertEquals("1990-01-01", result.getDateOfBirth());
        assertArrayEquals(new byte[]{1, 2, 3}, result.getAvatar());
        assertEquals("2023-01-01", result.getCreatedAccount());
    }

    @Test
    void testFindByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange: Tạo và lưu một user
        User user = User.builder()
                .email("biofittest@example.com")
                .hashPassword("encodedPassword")
                .createdAccount("2023-01-01")
                .build();
        userRepository.save(user);

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

    @Test
    void testFindByUserId_ShouldReturnUser_WhenUserIdExists() {
        // Arrange: Tạo và lưu một user
        User user = User.builder()
                .email("biofittest@example.com")
                .hashPassword("encodedPassword")
                .createdAccount("2023-01-01")
                .build();
        User savedUser = userRepository.save(user);
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

    @Test
    void testFindAll_ShouldReturnAllUsers_WhenUsersExist() {
        // Arrange: Tạo và lưu hai user
        User user1 = User.builder()
                .email("biofituser1@example.com")
                .hashPassword("pass1")
                .createdAccount("2023-01-01")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .email("biofituser2@example.com")
                .hashPassword("pass2")
                .createdAccount("2023-01-02")
                .build();
        userRepository.save(user2);

        // Act: Lấy tất cả user
        List<User> users = userRepository.findAll();

        // Assert: Kiểm tra danh sách
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("biofituser1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("biofituser2@example.com")));
    }

    @Test
    void testFindAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Act: Lấy tất cả user khi không có dữ liệu
        List<User> users = userRepository.findAll();

        // Assert: Kiểm tra danh sách rỗng
        assertTrue(users.isEmpty());
    }
}