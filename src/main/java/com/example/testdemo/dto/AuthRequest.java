package com.example.testdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 500, message = "Password size must be in range 8 - 500")
        String password
) {
}
