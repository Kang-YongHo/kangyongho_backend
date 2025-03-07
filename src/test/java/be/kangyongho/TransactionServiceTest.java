package be.kangyongho;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.kangyongho.bank.component.TransferFeeCalculator;
import be.kangyongho.bank.component.TransferLimitChecker;
import be.kangyongho.bank.component.WithdrawalLimitChecker;
import be.kangyongho.bank.dto.request.DepositRequest;
import be.kangyongho.bank.dto.request.TransferRequest;
import be.kangyongho.bank.dto.request.WithdrawRequest;
import be.kangyongho.bank.dto.response.TransactionDto;
import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.entity.Transaction;
import be.kangyongho.bank.repository.AccountRepository;
import be.kangyongho.bank.repository.TransactionRepository;
import be.kangyongho.bank.service.AccountService;
import be.kangyongho.bank.service.TransactionService;
import be.kangyongho.gobal.exception.InsufficientBalanceException;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private WithdrawalLimitChecker withdrawalLimitChecker;

    @Mock
    private TransferLimitChecker transferLimitChecker;

    @Mock
    private TransferFeeCalculator transferFeeCalculator;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account targetAccount;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        sourceAccount = Account.builder()
                .id(1L)
                .accountNumber("1111111111")
                .ownerName("홍길동")
                .balance(BigDecimal.valueOf(10000))
                .build();

        targetAccount = Account.builder()
                .id(2L)
                .accountNumber("2222222222")
                .ownerName("김철수")
                .balance(BigDecimal.valueOf(5000))
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(BigDecimal.valueOf(1000))
                .type(Transaction.TransactionType.TRANSFER)
                .description("테스트 거래")
                .build();
    }

    @Test
    void depositSuccessTest() {
        // Given
        DepositRequest request = new DepositRequest("2222222222", BigDecimal.valueOf(1000), "입금 테스트");

        when(accountService.findAccountByAccountNumberWithLock("2222222222")).thenReturn(targetAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionDto result = transactionService.deposit(request);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        verify(accountRepository).save(targetAccount);
        assertEquals(BigDecimal.valueOf(6000), targetAccount.getBalance());
    }

    @Test
    void withdrawSuccessTest() {
        // Given
        WithdrawRequest request = new WithdrawRequest("1111111111", BigDecimal.valueOf(1000), "출금 테스트");

        when(accountService.findAccountByAccountNumberWithLock("1111111111")).thenReturn(sourceAccount);
        doNothing().when(withdrawalLimitChecker).checkDailyWithdrawalLimit(sourceAccount, BigDecimal.valueOf(1000));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionDto result = transactionService.withdraw(request);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        verify(accountRepository).save(sourceAccount);
        assertEquals(BigDecimal.valueOf(9000), sourceAccount.getBalance());
    }

    @Test
    void withdrawInsufficientBalanceTest() {
        // Given
        WithdrawRequest request = new WithdrawRequest("1111111111", BigDecimal.valueOf(20000), "출금 테스트");

        when(accountService.findAccountByAccountNumberWithLock("1111111111")).thenReturn(sourceAccount);

        // When & Then
        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.withdraw(request);
        });
        
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transferSuccessTest() {
        // Given
        TransferRequest request = new TransferRequest(
            "1111111111", "2222222222", BigDecimal.valueOf(1000), "이체 테스트"
        );

        when(accountService.findAccountByAccountNumberWithLock("1111111111")).thenReturn(sourceAccount);
        when(accountService.findAccountByAccountNumberWithLock("2222222222")).thenReturn(targetAccount);
        doNothing().when(transferLimitChecker).checkDailyTransferLimit(sourceAccount, BigDecimal.valueOf(1000));
        when(transferFeeCalculator.calculateFee(BigDecimal.valueOf(1000))).thenReturn(BigDecimal.valueOf(10));
        when(transferFeeCalculator.calculateTotalWithFee(BigDecimal.valueOf(1000))).thenReturn(BigDecimal.valueOf(1010));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionDto result = transactionService.transfer(request);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        assertEquals(BigDecimal.valueOf(9000), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(6000), targetAccount.getBalance());
    }
}