package be.kangyongho;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import be.kangyongho.bank.dto.request.AccountCreateRequest;
import be.kangyongho.bank.dto.response.AccountDto;
import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.repository.AccountRepository;
import be.kangyongho.bank.repository.TransactionRepository;
import be.kangyongho.bank.service.AccountService;
import be.kangyongho.gobal.exception.AccountNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.valueOf(10000))
                .build();

        createRequest = new AccountCreateRequest("홍길동", BigDecimal.valueOf(10000));
    }

    @Test
    void createAccountSuccessTest() {
        // Given
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        AccountDto result = accountService.createAccount(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals("홍길동", result.getOwnerName());
        assertEquals(BigDecimal.valueOf(10000), result.getBalance());
        
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any());
    }

    @Test
    void getAccountByNumberSuccessTest() {
        // Given
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(testAccount));

        // When
        AccountDto result = accountService.getAccountByNumber("1234567890");

        // Then
        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals("홍길동", result.getOwnerName());
    }

    @Test
    void getAccountByNumberFailTest() {
        // Given
        when(accountRepository.findByAccountNumber("9999999999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByNumber("9999999999");
        });
    }

    @Test
    void getAllAccountsTest() {
        // Given
        Account secondAccount = Account.builder()
                .id(2L)
                .accountNumber("0987654321")
                .ownerName("김철수")
                .balance(BigDecimal.valueOf(20000))
                .build();
                
        when(accountRepository.findAll()).thenReturn(Arrays.asList(testAccount, secondAccount));

        // When
        List<AccountDto> results = accountService.getAllAccounts();

        // Then
        assertEquals(2, results.size());
        assertEquals("1234567890", results.get(0).getAccountNumber());
        assertEquals("0987654321", results.get(1).getAccountNumber());
    }
}