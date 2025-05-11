package com.example.testdemo.dto;

import com.example.testdemo.entity.Account;

public record TransferResult(
        Account fromAccount,
        Account toAccount
) {}
