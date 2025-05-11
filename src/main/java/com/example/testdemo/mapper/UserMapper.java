package com.example.testdemo.mapper;

import com.example.testdemo.dto.UserResponseDto;
import com.example.testdemo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "user.emailData.email", target = "email")
    @Mapping(source = "user.phoneData.phone", target = "phone")
    @Mapping(source = "user.account.balance", target = "balance")
    UserResponseDto toDto(User user);
}

