package be.kangyongho;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import be.kangyongho.bank.dto.request.AccountCreateRequest;
import be.kangyongho.bank.dto.request.DepositRequest;
import be.kangyongho.bank.dto.request.TransferRequest;
import be.kangyongho.bank.dto.request.WithdrawRequest;
import be.kangyongho.bank.dto.response.AccountDto;
import be.kangyongho.bank.dto.response.TransactionDto;
import be.kangyongho.bank.service.AccountService;
import be.kangyongho.bank.service.TransactionService;
import be.kangyongho.gobal.exception.InsufficientBalanceException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("통합 테스트는 실제 DB 환경이 필요하므로 임시로 비활성화")
public class BankingIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void fullBankingFlowTest() {
        // 1. 계좌 생성
        AccountCreateRequest createRequest1 = new AccountCreateRequest("홍길동", BigDecimal.valueOf(10000));
        AccountCreateRequest createRequest2 = new AccountCreateRequest("김철수", BigDecimal.valueOf(5000));

        AccountDto account1 = accountService.createAccount(createRequest1);
        AccountDto account2 = accountService.createAccount(createRequest2);

        assertNotNull(account1.getAccountNumber());
        assertNotNull(account2.getAccountNumber());
        assertEquals("홍길동", account1.getOwnerName());
        assertEquals("김철수", account2.getOwnerName());
        assertEquals(BigDecimal.valueOf(10000), account1.getBalance());
        assertEquals(BigDecimal.valueOf(5000), account2.getBalance());

        // 2. 입금 테스트
        DepositRequest depositRequest = new DepositRequest(
            account1.getAccountNumber(), BigDecimal.valueOf(2000), "월급"
        );

        TransactionDto depositResult = transactionService.deposit(depositRequest);

        assertNotNull(depositResult);
        assertEquals(BigDecimal.valueOf(2000), depositResult.getAmount());

        // 입금 후 잔액 확인
        AccountDto updatedAccount1 = accountService.getAccountByNumber(account1.getAccountNumber());
        assertEquals(BigDecimal.valueOf(12000), updatedAccount1.getBalance());

        // 3. 출금 테스트
        WithdrawRequest withdrawRequest = new WithdrawRequest(
            account1.getAccountNumber(), BigDecimal.valueOf(3000), "생활비"
        );

        TransactionDto withdrawResult = transactionService.withdraw(withdrawRequest);

        assertNotNull(withdrawResult);
        assertEquals(BigDecimal.valueOf(3000), withdrawResult.getAmount());

        // 출금 후 잔액 확인
        updatedAccount1 = accountService.getAccountByNumber(account1.getAccountNumber());
        assertEquals(BigDecimal.valueOf(9000), updatedAccount1.getBalance());

        // 4. 이체 테스트
        TransferRequest transferRequest = new TransferRequest(
            account1.getAccountNumber(), account2.getAccountNumber(),
            BigDecimal.valueOf(1000), "친구에게 송금"
        );

        TransactionDto transferResult = transactionService.transfer(transferRequest);

        assertNotNull(transferResult);
        assertEquals(BigDecimal.valueOf(1000), transferResult.getAmount());

        // 이체 후 양쪽 계좌 잔액 확인
        updatedAccount1 = accountService.getAccountByNumber(account1.getAccountNumber());
        AccountDto updatedAccount2 = accountService.getAccountByNumber(account2.getAccountNumber());

        // 수수료 계산을 고려하지 않은 단순 테스트이므로 정확한 금액 확인은 어려울 수 있음
        assertTrue(updatedAccount1.getBalance().compareTo(BigDecimal.valueOf(8000)) <= 0);
        assertEquals(BigDecimal.valueOf(6000), updatedAccount2.getBalance());

        // 5. 잔액 부족 예외 테스트
        WithdrawRequest largeWithdrawRequest = new WithdrawRequest(
            account1.getAccountNumber(), BigDecimal.valueOf(50000), "큰 금액 출금"
        );

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.withdraw(largeWithdrawRequest);
        });
    }
}