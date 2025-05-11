package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Transfer response data")
public record TransferResponse(
        @Schema(description = "Sender's new balance", example = "899.50")
        BigDecimal senderNewBalance,
        @Schema(description = "Recipient's new balance", example = "1100.50")
        BigDecimal recipientNewBalance,
        @Schema(description = "Operation result message", example = "Transfer completed successfully")
        String message
) {}
