package com.example.testdemo.service;

import com.example.testdemo.dto.TransferResult;
import com.example.testdemo.entity.Account;
import com.example.testdemo.entity.Transfer;
import com.example.testdemo.entity.User;
import com.example.testdemo.enums.TransferStatus;
import com.example.testdemo.exception.AccountNotFoundException;
import com.example.testdemo.exception.InsufficientFundsException;
import com.example.testdemo.exception.InvalidAmountException;
import com.example.testdemo.exception.InvalidTransferException;
import com.example.testdemo.exception.TransferLimitExceededException;
import com.example.testdemo.repository.AccountRepository;
import com.example.testdemo.repository.TransferRepository;
import com.example.testdemo.service.impl.BalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Account fromAccount;
    private Account toAccount;
    private User fromUser;
    private User toUser;

    @BeforeEach
    void setUp() {
        fromUser = new User();
        fromUser.setId(1L);
        fromUser.setName("User1");

        toUser = new User();
        toUser.setId(2L);
        toUser.setName("User2");

        fromAccount = Account.builder()
                .user(fromUser)
                .balance(new BigDecimal("1000.00"))
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        toAccount = Account.builder()
                .user(toUser)
                .balance(new BigDecimal("500.00"))
                .initialBalance(new BigDecimal("500.00"))
                .build();

        fromUser.setAccount(fromAccount);
        toUser.setAccount(toAccount);
    }

    @Test
    void transferMoney_SuccessfulTransfer() {
        // Arrange
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.saveAll(anyList())).thenReturn(List.of(fromAccount, toAccount));
        when(transferRepository.save(any(Transfer.class))).thenReturn(new Transfer());

        // Act
        TransferResult result = balanceService.transferMoney(1L, 2L, new BigDecimal("100.00"));

        // Assert
        assertEquals(new BigDecimal("900.00"), result.fromAccount().getBalance());
        assertEquals(new BigDecimal("600.00"), result.toAccount().getBalance());
        verify(transferRepository).save(argThat(transfer ->
                transfer.getStatus() == TransferStatus.COMPLETED));
    }

    @Test
    void transferMoney_AccountNotFound() {
        // Arrange
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () ->
                balanceService.transferMoney(1L, 2L, new BigDecimal("100.00")));
    }

    @Test
    void transferMoney_InsufficientFunds() {
        // Arrange
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(toAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                balanceService.transferMoney(1L, 2L, new BigDecimal("1500.00")));
    }

    @Test
    void transferMoney_InvalidAmount() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () ->
                balanceService.transferMoney(1L, 2L, new BigDecimal("-100.00")));
    }

    @Test
    void transferMoney_SameAccount() {
        // Act & Assert
        assertThrows(InvalidTransferException.class, () ->
                balanceService.transferMoney(1L, 1L, new BigDecimal("100.00")));
    }

    @Test
    void transferMoney_ExceedsLimit() {
        // Act & Assert
        assertThrows(TransferLimitExceededException.class, () ->
                balanceService.transferMoney(1L, 2L, new BigDecimal("1000001.00")));
    }

    @Test
    void transferMoney_ExactlyAtLimit() {
        // Arrange
        BigDecimal limitAmount = new BigDecimal("1000000.00");
        when(accountRepository.findByUserIdWithLock(fromUser.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUser.getId())).thenReturn(Optional.of(toAccount));
        when(accountRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        assertDoesNotThrow(() ->
                balanceService.transferMoney(fromUser.getId(), toUser.getId(), limitAmount));

        // Verify the transfer was processed
        verify(transferRepository).save(any(Transfer.class));
        verify(accountRepository).saveAll(anyList());
    }
}