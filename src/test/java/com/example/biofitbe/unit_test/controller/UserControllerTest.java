package com.example.biofitbe.unit_test.controller;

import com.example.biofitbe.controller.UserController;
import com.example.biofitbe.dto.*;
import com.example.biofitbe.model.User;
import com.example.biofitbe.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    // 1. Kiểm thử GET /api/user/
    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(new User()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    // 2. Kiểm thử GET /api/user/{email}
    @Test
    void testGetUserByEmail() throws Exception {
        String email = "biofittest@example.com";
        User user = new User();
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{email}", email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{email}", email))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // 3. Kiểm thử POST /api/user/login
    @Test
    void testLoginUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        when(userService.loginUser(any(LoginRequest.class))).thenReturn(Optional.of(userDTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofittest@example.com\",\"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofittest@example.com\",\"password\":\"wrong\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid email or password"));
    }

    // 4. Kiểm thử POST /api/user/register
    @Test
    void testRegisterUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(Optional.of(userDTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofitnew@example.com\",\"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofitexisting@example.com\",\"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email already exists"));
    }

    // 5. Kiểm thử PUT /api/user/update/{userId}
    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;

        // Tạo phần "user" dưới dạng JSON
        String userJson = "{\"name\":\"new name\"}";
        MockMultipartFile userPart = new MockMultipartFile(
                "user",           // Tên của part (phải khớp với controller)
                "",               // Tên file (có thể để trống vì đây không phải file thật)
                "application/json", // Loại nội dung
                userJson.getBytes() // Nội dung JSON
        );

        // Tạo phần "avatar" (giả lập file ảnh)
        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",         // Tên của part
                "avatar.jpg",     // Tên file
                MediaType.IMAGE_JPEG_VALUE, // Loại nội dung
                "avatar".getBytes() // Nội dung file (giả lập)
        );

        // Mock UserService trả về Optional.of(userDTO)
        UserDTO userDTO = new UserDTO();
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class), any())).thenReturn(Optional.of(userDTO));

        // Gửi yêu cầu PUT với cả hai part
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update/{userId}", userId)
                        .file(avatar)     // Thêm part avatar
                        .file(userPart)   // Thêm part user
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PUT"); // Chuyển sang phương thức PUT
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        // Mock UserService trả về Optional.empty()
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class), any())).thenReturn(Optional.empty());

        // Gửi yêu cầu PUT với cả hai part
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update/{userId}", userId)
                        .file(avatar)     // Thêm part avatar
                        .file(userPart)   // Thêm part user
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PUT"); // Chuyển sang phương thức PUT
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
    }

    // 6. Kiểm thử POST /api/user/forgot-password
    @Test
    void testRequestPasswordReset() throws Exception {
        PasswordResetResponse response = PasswordResetResponse.builder()
                .message("Password reset link sent")
                .success(true)
                .resetCode("abc123")
                .build();
        when(userService.requestPasswordReset(any(PasswordResetRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofittest@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password reset link sent"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resetCode").value("abc123"));

        response = PasswordResetResponse.builder()
                .message("User not found")
                .success(false)
                .build();
        when(userService.requestPasswordReset(any(PasswordResetRequest.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"biofitunknown@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
    }

    // 7. Kiểm thử POST /api/user/reset-password
    @Test
    void testResetPassword() throws Exception {
        PasswordResetResponse response = PasswordResetResponse.builder()
                .message("Password reset successfully")
                .success(true)
                .build();
        when(userService.resetPassword(any(PasswordResetConfirm.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"valid-token\",\"newPassword\":\"new-password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password reset successfully"));

        response = PasswordResetResponse.builder()
                .message("Invalid token")
                .success(false)
                .build();
        when(userService.resetPassword(any(PasswordResetConfirm.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"invalid-token\",\"newPassword\":\"new-password\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid token"));
    }
}
