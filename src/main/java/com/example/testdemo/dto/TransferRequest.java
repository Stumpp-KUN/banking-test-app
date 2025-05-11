package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Transfer request data")
public record TransferRequest(
        @Schema(description = "Recipient user ID", example = "2")
        @NotNull(message = "Recipient ID cannot be null")
        Long userIdTo,
        @Schema(description = "Transfer amount", example = "100.50")
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        @Digits(integer = 12, fraction = 2, message = "Amount must have up to 12 digits and 2 decimal places")
        BigDecimal value
) {
}
