package com.example.testdemo.repository;

import com.example.testdemo.entity.EmailData;
import com.example.testdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    Optional<EmailData> findByEmailAndUser(String email, User user);
    boolean existsByEmailAndUserNot(String email, User user);
    boolean existsByEmailAndUser(String email, User user);
    void deleteByEmailAndUser(String email, User user);
}

