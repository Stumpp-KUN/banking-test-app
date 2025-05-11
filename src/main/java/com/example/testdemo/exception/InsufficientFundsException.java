package com.example.testdemo.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(BigDecimal currentBalance, BigDecimal requestedAmount) {
        super(String.format("Insufficient funds. Current balance: %.2f, requested amount: %.2f",
                currentBalance, requestedAmount));
    }
}
