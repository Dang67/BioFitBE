package com.example.biofitbe.controller;

import com.example.biofitbe.dto.*;
import com.example.biofitbe.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<UserDTO> userDTO = userService.loginUser(loginRequest);

        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get()); // Thành công, trả về UserDTO
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid email or password")); // Lỗi, trả về JSON
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        Optional<UserDTO> userDTO = userService.registerUser(registerRequest);

        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get()); // Thành công, trả về UserDTO
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "Email already exists")); // Lỗi, trả về JSON
        }
    }

    /*@PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest updateUserRequest) {

        Optional<UserDTO> updatedUser = userService.updateUser(userId, updateUserRequest);

        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser.get()); // Trả về thông tin user sau khi cập nhật
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User not found"));
        }
    }*/

    @PutMapping(value = "/update/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart("user") UpdateUserRequest updateUserRequest) {

        Optional<UserDTO> updatedUser = userService.updateUser(userId, updateUserRequest, avatar);

        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser.get()); // Trả về thông tin user sau khi cập nhật
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User not found"));
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> requestPasswordReset(
            @RequestBody PasswordResetRequest requestDTO) {
        PasswordResetResponse response = userService.requestPasswordReset(requestDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @RequestBody PasswordResetConfirm confirmDTO) {
        PasswordResetResponse response = userService.resetPassword(confirmDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}