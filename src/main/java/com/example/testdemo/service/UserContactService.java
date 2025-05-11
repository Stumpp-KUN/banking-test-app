package com.example.testdemo.service;

import com.example.testdemo.dto.EmailUpdateRequest;
import com.example.testdemo.dto.PhoneUpdateRequest;

public interface UserContactService {
    void addEmail(Long userId, EmailUpdateRequest request);
    void updateEmail(Long userId, String oldEmail, EmailUpdateRequest request);
    void deleteEmail(Long userId, String email);
    void addPhone(Long userId, PhoneUpdateRequest request);
    void updatePhone(Long userId, String oldPhone, PhoneUpdateRequest request);
    void deletePhone(Long userId, String phone);
}
