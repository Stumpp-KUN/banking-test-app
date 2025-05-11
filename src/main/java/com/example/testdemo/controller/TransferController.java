package com.example.testdemo.controller;

import com.example.testdemo.dto.TransferRequest;
import com.example.testdemo.dto.TransferResponse;
import com.example.testdemo.dto.TransferResult;
import com.example.testdemo.exception.ResourceNotFoundException;
import com.example.testdemo.repository.UserRepository;
import com.example.testdemo.service.BalanceService;
import com.example.testdemo.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transfer API", description = "Operations with money transfers")
@RequiredArgsConstructor
public class TransferController {
    private final BalanceService balanceService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Transfer money between accounts",
            description = "Authenticated user can transfer money to another user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Recipient not found")
    })
    @PostMapping
    public ResponseEntity<TransferResponse> transferMoney(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid TransferRequest request) {

        String token = authHeader.substring(7);
        Long fromUserId = jwtService.extractUserId(token);

        if (!userRepository.existsById(request.userIdTo())) {
            throw new ResourceNotFoundException("Recipient user not found");
        }

        TransferResult result = balanceService.transferMoney(
                fromUserId,
                request.userIdTo(),
                request.value()
        );

        return ResponseEntity.ok(new TransferResponse(
                result.fromAccount().getBalance(),
                result.toAccount().getBalance(),
                "Transfer completed successfully"
        ));
    }
}