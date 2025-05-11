package com.example.testdemo.controller;

import com.example.testdemo.dto.EmailUpdateRequest;
import com.example.testdemo.dto.PhoneUpdateRequest;
import com.example.testdemo.service.UserContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/contacts")
@RequiredArgsConstructor
@Tag(name = "User Contacts API", description = "Operations with user contact information (emails and phones)")
public class UserContactController {
    private final UserContactService userContactService;

    @Operation(
            summary = "Add email",
            description = "Adds a new email address for the user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PostMapping("/emails")
    public ResponseEntity<Void> addEmail(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @RequestBody @Valid EmailUpdateRequest request) {
        userContactService.addEmail(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update email",
            description = "Updates existing user's email address",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "New email already in use")
    })
    @PutMapping("/emails/{oldEmail}")
    public ResponseEntity<Void> updateEmail(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Current email address", example = "old@example.com")
            @PathVariable String oldEmail,

            @RequestBody @Valid EmailUpdateRequest request) {
        userContactService.updateEmail(userId, oldEmail, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete email",
            description = "Deletes user's email address (cannot delete the last email)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Email deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete last email")
    })
    @DeleteMapping("/emails/{email}")
    public ResponseEntity<Void> deleteEmail(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Email address to delete", example = "user@example.com")
            @PathVariable String email) {
        userContactService.deleteEmail(userId, email);
        return ResponseEntity.noContent().build();
    }

    // Phone endpoints with similar documentation
    @Operation(
            summary = "Add phone",
            description = "Adds a new phone number for the user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Phone already in use")
    })
    @PostMapping("/phones")
    public ResponseEntity<Void> addPhone(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @RequestBody @Valid PhoneUpdateRequest request) {
        userContactService.addPhone(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update phone",
            description = "Updates existing user's phone number",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Phone not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "New phone already in use")
    })
    @PutMapping("/phones/{oldPhone}")
    public ResponseEntity<Void> updatePhone(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Current phone number", example = "+1234567890")
            @PathVariable String oldPhone,

            @RequestBody @Valid PhoneUpdateRequest request) {
        userContactService.updatePhone(userId, oldPhone, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete phone",
            description = "Deletes user's phone number (cannot delete the last phone)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Phone deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Phone not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete last phone")
    })
    @DeleteMapping("/phones/{phone}")
    public ResponseEntity<Void> deletePhone(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Phone number to delete", example = "+1234567890")
            @PathVariable String phone) {
        userContactService.deletePhone(userId, phone);
        return ResponseEntity.noContent().build();
    }
}
