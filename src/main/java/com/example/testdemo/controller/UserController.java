package com.example.testdemo.controller;

import com.example.testdemo.dto.UserResponseDto;
import com.example.testdemo.entity.User;
import com.example.testdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThan;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Search API", description = "Search users with filtering options")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Search users",
            description = "Search users with filtering by various fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<Page<UserResponseDto>> findUsers(
            @Join(path= "emails", alias = "email")
            @Join(path= "phones", alias = "phones")
            @And({
                    @Spec(path = "dateOfBirth", spec = GreaterThan.class),
                    @Spec(path = "phone.phone", spec = Equal.class),
                    @Spec(path = "email.email", spec = Equal.class),
                    @Spec(path = "name", spec = Like.class)
            }) Specification<User> spec,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(userService.findUsers(spec, pageable));
    }
}

