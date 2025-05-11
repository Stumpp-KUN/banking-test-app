package com.example.testdemo.service.impl;

import com.example.testdemo.dto.TransferResult;
import com.example.testdemo.entity.Account;
import com.example.testdemo.entity.Transfer;
import com.example.testdemo.enums.TransferStatus;
import com.example.testdemo.exception.*;
import com.example.testdemo.repository.AccountRepository;
import com.example.testdemo.repository.TransferRepository;
import com.example.testdemo.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResult transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        validateTransferRequest(fromUserId, toUserId, amount);

        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
                .orElseThrow(() -> new AccountNotFoundException(fromUserId));
        Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
                .orElseThrow(() -> new AccountNotFoundException(toUserId));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromAccount.getBalance(), amount);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.saveAll(List.of(fromAccount, toAccount));

        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .status(TransferStatus.COMPLETED)
                .build();
        transferRepository.save(transfer);

        return new TransferResult(fromAccount, toAccount);
    }

    private void validateTransferRequest(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (fromUserId.equals(toUserId)) {
            throw new InvalidTransferException("Cannot transfer to yourself");
        }

        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new TransferLimitExceededException("Transfer amount exceeds maximum limit");
        }
    }
}
