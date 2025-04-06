package com.example.biofitbe.unit_test.service;

import com.example.biofitbe.dto.*;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.UserRepository;
import com.example.biofitbe.service.ExerciseService;
import com.example.biofitbe.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Không cần thêm gì ở đây vì MockitoExtension tự động khởi tạo các mock
    }

    // 1. Test getAllUsers
    @Test
    void testGetAllUsers() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    // 2. Test getUserByEmail
    @Test
    void testGetUserByEmail() {
        String email = "biofittest@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findByEmail(email);

        // Trường hợp không tìm thấy user
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        result = userService.getUserByEmail(email);
        assertFalse(result.isPresent());
    }

    // 3. Test loginUser
    @Test
    void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("biofittest@example.com");
        loginRequest.setPassword("plainPassword");

        User user = new User();
        user.setHashPassword("encodedPassword");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        Optional<UserDTO> result = userService.loginUser(loginRequest);

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches("plainPassword", "encodedPassword");

        // Trường hợp mật khẩu không khớp
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(false);
        result = userService.loginUser(loginRequest);
        assertFalse(result.isPresent());
    }

    // 4. Test registerUser
    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("biofitnew@example.com");
        request.setPassword("plainPassword");

        // Mock kiểm tra email chưa tồn tại
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // Mock userRepository.save để trả về User với userId được gán
        User savedUser = new User();
        savedUser.setUserId(1L); // Gán userId giả lập
        savedUser.setEmail(request.getEmail());
        savedUser.setHashPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Gọi phương thức cần test
        Optional<UserDTO> result = userService.registerUser(request);

        // Kiểm tra kết quả
        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
        verify(exerciseService, times(1)).initializeDefaultExercises(1L);

        // Trường hợp email đã tồn tại
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));
        result = userService.registerUser(request);
        assertFalse(result.isPresent());
        verify(userRepository, times(2)).findByEmail(request.getEmail()); // Gọi lần thứ hai
    }

    // 5. Test updateUser
    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<UserDTO> result = userService.updateUser(userId, request, null);

        assertTrue(result.isPresent());
        assertEquals("New Name", user.getFullName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);

        // Trường hợp không tìm thấy user
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        result = userService.updateUser(userId, request, null);
        assertFalse(result.isPresent());
    }

    // 6. Test requestPasswordReset
    @Test
    void testRequestPasswordReset() {
        PasswordResetRequest requestDTO = new PasswordResetRequest();
        requestDTO.setEmail("biofittest@example.com");

        User user = new User();
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetResponse result = userService.requestPasswordReset(requestDTO);

        assertTrue(result.isSuccess());
        assertEquals("Password reset code sent to your email. Valid for 30 minutes.", result.getMessage());
        assertNotNull(result.getResetCode());
        assertEquals(6, result.getResetCode().length());
        verify(userRepository, times(1)).findByEmail(requestDTO.getEmail());
        verify(userRepository, times(1)).save(user);

        // Trường hợp không tìm thấy user
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.empty());
        result = userService.requestPasswordReset(requestDTO);
        assertFalse(result.isSuccess());
        assertEquals("User with this email not found.", result.getMessage());
    }

    // 7. Test resetPassword
    @Test
    void testResetPassword() {
        PasswordResetConfirm confirmDTO = new PasswordResetConfirm();
        confirmDTO.setEmail("biofittest@example.com");
        confirmDTO.setResetCode("123456");
        confirmDTO.setNewPassword("newPlainPassword");

        User user = new User();
        user.setResetCode("123456");
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10)); // Chưa hết hạn
        when(userRepository.findByEmail(confirmDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPlainPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetResponse result = userService.resetPassword(confirmDTO);

        assertTrue(result.isSuccess());
        assertEquals("Password has been reset successfully.", result.getMessage());
        assertNull(user.getResetCode()); // Đã bị xóa
        assertNull(user.getResetCodeExpiry()); // Đã bị xóa
        verify(passwordEncoder, times(1)).encode("newPlainPassword");
        verify(userRepository, times(1)).save(user);

        // Trường hợp mã reset không hợp lệ
        user.setResetCode("wrongCode");
        result = userService.resetPassword(confirmDTO);
        assertFalse(result.isSuccess());
        assertEquals("Invalid reset code.", result.getMessage());

        // Trường hợp mã reset hết hạn
        user.setResetCode("123456");
        user.setResetCodeExpiry(LocalDateTime.now().minusMinutes(1)); // Đã hết hạn
        result = userService.resetPassword(confirmDTO);
        assertFalse(result.isSuccess());
        assertEquals("Reset code has expired. Please request a new one.", result.getMessage());

        // Trường hợp mật khẩu mới không hợp lệ
        user.setResetCode("123456");
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10));
        confirmDTO.setNewPassword("short");
        result = userService.resetPassword(confirmDTO);
        assertFalse(result.isSuccess());
        assertEquals("Password must be at least 6 characters long.", result.getMessage());
    }
}
