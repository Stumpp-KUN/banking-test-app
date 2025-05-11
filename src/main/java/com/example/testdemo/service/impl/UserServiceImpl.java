package com.example.testdemo.service.impl;

import com.example.testdemo.dto.UserResponseDto;
import com.example.testdemo.entity.User;
import com.example.testdemo.mapper.UserMapper;
import com.example.testdemo.repository.UserRepository;
import com.example.testdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "userSearch")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "{#spec.toString(), #pageable}")
    public Page<UserResponseDto> findUsers(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

}

