package com.example.testdemo.repository;

import com.example.testdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailDataEmail(String email);
    Optional<User> findByPhoneDataPhone(String phone);
    Optional<User> findByName(String name);
    Optional<User> findByEmailOrPhone(String email, String phone);

}

