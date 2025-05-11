package com.example.testdemo.exception;

public class InvalidAmountException extends ResourceNotFoundException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
