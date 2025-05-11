package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Phone update request")
public record PhoneUpdateRequest(
        @NotBlank(message = "Phone cannot be blank")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone should be valid")
        String phone
) {
}
