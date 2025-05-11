package com.example.testdemo.service.impl;

import com.example.testdemo.dto.EmailUpdateRequest;
import com.example.testdemo.dto.PhoneUpdateRequest;
import com.example.testdemo.dto.UserContactsDto;
import com.example.testdemo.entity.EmailData;
import com.example.testdemo.entity.PhoneData;
import com.example.testdemo.entity.User;
import com.example.testdemo.exception.ConflictException;
import com.example.testdemo.exception.ResourceNotFoundException;
import com.example.testdemo.repository.EmailDataRepository;
import com.example.testdemo.repository.PhoneDataRepository;
import com.example.testdemo.repository.UserRepository;
import com.example.testdemo.service.UserContactService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "userContacts")
public class UserContactServiceImpl implements UserContactService {
    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void addEmail(Long userId, EmailUpdateRequest request) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        if (emailDataRepository.existsByEmailAndUserNot(request.email(), currentUser)) {
            throw new ConflictException("Email is already in use by another user");
        }

        EmailData email = new EmailData();
        email.setEmail(request.email());
        email.setUser(currentUser);
        emailDataRepository.save(email);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void updateEmail(Long userId, String oldEmail, EmailUpdateRequest request) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        EmailData email = emailDataRepository.findByEmailAndUser(oldEmail, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Old email not found"));

        if (!email.getEmail().equals(request.email()) &&
                emailDataRepository.existsByEmailAndUserNot(request.email(), currentUser)) {
            throw new ConflictException("Email is already in use by another user");
        }

        email.setEmail(request.email());
        emailDataRepository.save(email);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void deleteEmail(Long userId, String email) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        if (!emailDataRepository.existsByEmailAndUser(email, currentUser)) {
            throw new ResourceNotFoundException("Email not found for this user");
        }

        if (currentUser.getEmails().size() <= 1) {
            throw new ConflictException("Cannot delete the last email");
        }

        emailDataRepository.deleteByEmailAndUser(email, currentUser);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void addPhone(Long userId, PhoneUpdateRequest request) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        if (phoneDataRepository.existsByPhoneAndUserNot(request.phone(), currentUser)) {
            throw new ConflictException("Phone is already in use by another user");
        }

        PhoneData newPhone = PhoneData.builder()
                .phone(request.phone())
                .user(currentUser)
                .build();

        phoneDataRepository.save(newPhone);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void updatePhone(Long userId, String oldPhone, PhoneUpdateRequest request) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        PhoneData phoneData = phoneDataRepository.findByPhoneAndUser(oldPhone, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        if (!phoneData.getPhone().equals(request.phone()) &&
                phoneDataRepository.existsByPhoneAndUserNot(request.phone(), currentUser)) {
            throw new ConflictException("Phone is already in use by another user");
        }

        phoneData.setPhone(request.phone());
        phoneDataRepository.save(phoneData);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void deletePhone(Long userId, String phone) {
        User currentUser = getAuthenticatedUser();
        validateUserAccess(currentUser, userId);

        List<PhoneData> userPhones = currentUser.getPhones();
        if (userPhones.size() <= 1) {
            throw new ConflictException("Cannot delete the last phone");
        }

        if (!phoneDataRepository.existsByPhoneAndUser(phone, currentUser)) {
            throw new ResourceNotFoundException("Phone not found for this user");
        }

        phoneDataRepository.deleteByPhoneAndUser(phone, currentUser);
    }

    @Cacheable(key = "#userId", unless = "#result == null")
    public UserContactsDto getUserContacts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserContactsDto.builder()
                .emails(user.getEmails().stream().map(EmailData::getEmail).toList())
                .phones(user.getPhones().stream().map(PhoneData::getPhone).toList())
                .build();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));
    }

    private void validateUserAccess(User currentUser, Long targetUserId) {
        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("You can only modify your own data");
        }
    }
}
