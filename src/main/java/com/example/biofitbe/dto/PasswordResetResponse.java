package com.example.biofitbe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PasswordResetResponse {
    private String message;
    private boolean success;
    private String resetCode;
}