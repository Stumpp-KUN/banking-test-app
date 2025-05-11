package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "User information response")
public class UserResponseDto {
    Long id;
    String name;
    LocalDate dateOfBirth;
    String email;
    String phone;
    BigDecimal balance;
}

