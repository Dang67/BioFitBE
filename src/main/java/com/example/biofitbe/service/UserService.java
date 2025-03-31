package com.example.biofitbe.service;

import com.example.biofitbe.dto.*;
import com.example.biofitbe.model.*;
import com.example.biofitbe.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserDTO> loginUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getHashPassword())) {
                return Optional.of(UserDTO.fromEntity(user));
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<UserDTO> registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return Optional.empty();
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setHashPassword(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        user.setCreatedAccount(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        User savedUser = userRepository.save(user);
        return Optional.of(UserDTO.fromEntity(savedUser));
    }

    /*@Transactional
    public Optional<UserDTO> updateUser(Long userId, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (request.getFullName() != null) user.setFullName(request.getFullName());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getGender() != null) user.setGender(request.getGender());
            if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
            if (request.getHeight() != null) user.setHeight(request.getHeight());
            if (request.getWeight() != null) user.setWeight(request.getWeight());
            if (request.getTargetWeight() != null) user.setTargetWeight(request.getTargetWeight());
            if (request.getAvatar() != null) user.setAvatar(request.getAvatar());

            userRepository.save(user);

            return Optional.of(UserDTO.fromEntity(user));
        }

        return Optional.empty();
    }*/

    @Transactional
    public Optional<UserDTO> updateUser(Long userId, UpdateUserRequest request, MultipartFile avatar) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isUpdated = false;

            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
                isUpdated = true;
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
                isUpdated = true;
            }
            if (request.getGender() != null) {
                user.setGender(request.getGender());
                isUpdated = true;
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
                isUpdated = true;
            }
            if (request.getHeight() != null) {
                user.setHeight(request.getHeight());
                isUpdated = true;
            }
            if (request.getWeight() != null) {
                user.setWeight(request.getWeight());
                isUpdated = true;
            }
            if (request.getTargetWeight() != null) {
                user.setTargetWeight(request.getTargetWeight());
                isUpdated = true;
            }

            if (avatar != null && !avatar.isEmpty()) {
                try {
                    byte[] avatarBytes = avatar.getBytes();
                    System.out.println("Received avatar with size: " + avatarBytes.length);
                    user.setAvatar(avatarBytes);
                    isUpdated = true;
                } catch (IOException e) {
                    System.out.println("Error reading avatar file" + e.getMessage());
                    throw new RuntimeException("Error reading avatar file", e);
                }
            } else {
                System.out.println("No avatar received in request");
            }

            if (isUpdated) {
                userRepository.save(user);
            }

            return Optional.of(UserDTO.fromEntity(user));
        }

        return Optional.empty();
    }

    @Transactional
    public PasswordResetResponse requestPasswordReset(PasswordResetRequest requestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(requestDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generate a random 6-digit code
            String resetCode = String.format("%06d", new Random().nextInt(999999));

            // Set expiry time (30 minutes from now)
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);

            user.setResetCode(resetCode);
            user.setResetCodeExpiry(expiryTime);

            userRepository.save(user);

            // ghi nhật ký trong ter
            System.out.println("Reset code for " + requestDTO.getEmail() + ": " + resetCode);

            return PasswordResetResponse.builder()
                    .success(true)
                    .message("Password reset code sent to your email. Valid for 30 minutes.")
                    .resetCode(resetCode)
                    .build();
        }

        return PasswordResetResponse.builder()
                .success(false)
                .message("User with this email not found.")
                .build();
    }

    @Transactional
    public PasswordResetResponse resetPassword(PasswordResetConfirm confirmDTO) {
        Optional<User> userOptional = userRepository.findByEmail(confirmDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Kiểm tra xem mã reset có hợp lệ ko
            if (user.getResetCode() == null || !user.getResetCode().equals(confirmDTO.getResetCode())) {
                return PasswordResetResponse.builder()
                        .success(false)
                        .message("Invalid reset code.")
                        .build();
            }

            if (user.getResetCodeExpiry() == null || user.getResetCodeExpiry().isBefore(LocalDateTime.now())) {
                return PasswordResetResponse.builder()
                        .success(false)
                        .message("Reset code has expired. Please request a new one.")
                        .build();
            }

            // Xác thực mật khẩu
            if (confirmDTO.getNewPassword() == null || confirmDTO.getNewPassword().length() < 6) {
                return PasswordResetResponse.builder()
                        .success(false)
                        .message("Password must be at least 6 characters long.")
                        .build();
            }

            // Mã hóa mật khẩu mới
            user.setHashPassword(passwordEncoder.encode(confirmDTO.getNewPassword()));

            // Xóa mã reset
            user.setResetCode(null);
            user.setResetCodeExpiry(null);

            userRepository.save(user);

            return PasswordResetResponse.builder()
                    .success(true)
                    .message("Password has been reset successfully.")
                    .build();
        }

        return PasswordResetResponse.builder()
                .success(false)
                .message("User with this email not found.")
                .build();
    }
}