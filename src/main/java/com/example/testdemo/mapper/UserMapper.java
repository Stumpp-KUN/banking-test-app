package com.example.testdemo.mapper;

import com.example.testdemo.dto.UserResponseDto;
import com.example.testdemo.entity.EmailData;
import com.example.testdemo.entity.PhoneData;
import com.example.testdemo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "emails", target = "email", qualifiedByName = "emailsToEmailList")
    @Mapping(source = "phones", target = "phone", qualifiedByName = "phonesToPhoneList")
    @Mapping(source = "account.balance", target = "balance")
    UserResponseDto toDto(User user);

    @Named("emailsToEmailList")
    static List<String> emailsToEmailList(List<EmailData> emails) {
        if (emails == null) {
            return null;
        }
        return emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toList());
    }

    @Named("phonesToPhoneList")
    static List<String> phonesToPhoneList(List<PhoneData> phones) {
        if (phones == null) {
            return null;
        }
        return phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toList());
    }
}

