package com.example.testdemo.service;

import com.example.testdemo.dto.TransferResult;

import java.math.BigDecimal;

public interface BalanceService {
    TransferResult transferMoney(Long fromUserId, Long toUserId, BigDecimal amount);
}
