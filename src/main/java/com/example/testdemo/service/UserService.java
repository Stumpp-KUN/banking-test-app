package com.example.testdemo.service;

import com.example.testdemo.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    Page<UserResponseDto> findUsers(Specification spec, Pageable pageable);
}

