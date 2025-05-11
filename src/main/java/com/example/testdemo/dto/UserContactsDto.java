package com.example.testdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "User contacts information")
public record UserContactsDto(
        List<String> emails,
        List<String> phones
) {}
