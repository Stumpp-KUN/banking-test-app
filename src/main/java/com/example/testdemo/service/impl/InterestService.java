package com.example.testdemo.service.impl;

import com.example.testdemo.entity.Account;
import com.example.testdemo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final AccountRepository accountRepository;
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void applyInterest() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal initialBalance = account.getInitialBalance();
            BigDecimal maxAllowedBalance = initialBalance.multiply(MAX_MULTIPLIER)
                    .setScale(2, RoundingMode.HALF_UP);

            if (currentBalance.compareTo(maxAllowedBalance) < 0) {
                BigDecimal newBalance = currentBalance.multiply(BigDecimal.ONE.add(INTEREST_RATE))
                        .setScale(2, RoundingMode.HALF_UP);

                if (newBalance.compareTo(maxAllowedBalance) > 0) {
                    newBalance = maxAllowedBalance;
                }

                account.setBalance(newBalance);
                accountRepository.save(account);
            }
        }
    }
}