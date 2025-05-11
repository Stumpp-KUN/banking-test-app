package com.example.testdemo.exception;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(Long userId) {
        super("Account not found for user ID: " + userId);
    }
}
