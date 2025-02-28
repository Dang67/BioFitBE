package com.example.biofitbe.model;

import lombok.*;

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

    public static UserDTO fromEntity(User user) {
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
    }
}