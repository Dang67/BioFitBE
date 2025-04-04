package com.example.biofitbe.dto;

import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.User;
import lombok.*;

import java.util.Base64;
import java.util.Comparator;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private String fullName;
    private Integer gender;
    private Float height;
    private Float weight;
    private Float targetWeight;
    private String dateOfBirth;
    private String avatar;
    private String createdAccount;
    private String paymentStatus;

    /*public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .targetWeight(user.getTargetWeight())
                .dateOfBirth(user.getDateOfBirth())
                .avatar(user.getAvatar())
                .createdAccount(user.getCreatedAccount())
                .build();
    }*/
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.userId = user.getUserId();
        dto.fullName = user.getFullName();
        dto.email = user.getEmail();
        dto.gender = user.getGender();
        dto.dateOfBirth = user.getDateOfBirth();
        dto.height = user.getHeight();
        dto.weight = user.getWeight();
        dto.targetWeight = user.getTargetWeight();
        dto.createdAccount = user.getCreatedAccount();

        // Chuyển đổi ảnh từ byte[] sang Base64
        if (user.getAvatar() != null) {
            dto.avatar = Base64.getEncoder().encodeToString(user.getAvatar());
        } else {
            dto.avatar = null;
        }

        // Find the latest completed payment for this user
        Optional<Payment> latestCompletedPayment = user.getPayments().stream()
                .filter(payment -> "COMPLETED".equals(payment.getPaymentStatus()))
                .max(Comparator.comparing(Payment::getPaidAt));

        // Set payment status
        dto.paymentStatus = latestCompletedPayment.isPresent() ? "COMPLETED" : "PENDING";

        // Chuyển đổi ảnh từ byte[] sang Base64
        if (user.getAvatar() != null) {
            dto.avatar = Base64.getEncoder().encodeToString(user.getAvatar());
        } else {
            dto.avatar = null;
        }

        return dto;
    }

}