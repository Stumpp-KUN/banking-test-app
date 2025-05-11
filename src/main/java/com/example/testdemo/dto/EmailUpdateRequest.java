package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Email update request")
public record EmailUpdateRequest (
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email
) {

}
