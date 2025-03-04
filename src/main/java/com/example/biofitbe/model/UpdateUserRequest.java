package com.example.biofitbe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private Integer gender;
    private String dateOfBirth;
    private Float height;
    private Float weight;
    private Float targetWeight;
    private String avatar;
}