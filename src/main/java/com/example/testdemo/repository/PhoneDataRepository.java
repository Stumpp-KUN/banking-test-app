package com.example.testdemo.repository;

import com.example.testdemo.entity.PhoneData;
import com.example.testdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndUserNot(String phone, User user);
    boolean existsByPhoneAndUser(String phone, User user);
    Optional<PhoneData> findByPhoneAndUser(String phone, User user);
    void deleteByPhoneAndUser(String phone, User user);
}

