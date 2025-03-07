package be.kangyongho;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.kangyongho.bank.controller.AccountApiController;
import be.kangyongho.bank.dto.request.AccountCreateRequest;
import be.kangyongho.bank.dto.response.AccountDto;
import be.kangyongho.bank.service.AccountService;
import be.kangyongho.gobal.exception.AccountNotFoundException;

@WebMvcTest(AccountApiController.class)
public class AccountApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountDto accountDto;
    private AccountCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        accountDto = AccountDto.builder()
            .id(1L)
            .accountNumber("1234567890")
            .ownerName("홍길동")
            .balance(BigDecimal.valueOf(10000))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        createRequest = new AccountCreateRequest("홍길동", BigDecimal.valueOf(10000));
    }

    @Test
    void createAccountSuccessTest() throws Exception {
        when(accountService.createAccount(any(AccountCreateRequest.class))).thenReturn(accountDto);

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").value("1234567890"))
            .andExpect(jsonPath("$.ownerName").value("홍길동"))
            .andExpect(jsonPath("$.balance").value(10000));
    }

    @Test
    void getAccountByNumberSuccessTest() throws Exception {
        when(accountService.getAccountByNumber("1234567890")).thenReturn(accountDto);

        mockMvc.perform(get("/api/v1/accounts/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value("1234567890"))
            .andExpect(jsonPath("$.ownerName").value("홍길동"))
            .andExpect(jsonPath("$.balance").value(10000));
    }

    @Test
    void getAccountByNumberNotFoundTest() throws Exception {
        when(accountService.getAccountByNumber("9999999999"))
            .thenThrow(new AccountNotFoundException("9999999999", "accountNumber"));

        mockMvc.perform(get("/api/v1/accounts/9999999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllAccountsTest() throws Exception {
        AccountDto secondAccountDto = AccountDto.builder()
            .id(2L)
            .accountNumber("0987654321")
            .ownerName("김철수")
            .balance(BigDecimal.valueOf(20000))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        List<AccountDto> accounts = Arrays.asList(accountDto, secondAccountDto);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/v1/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
            .andExpect(jsonPath("$[1].accountNumber").value("0987654321"))
            .andExpect(jsonPath("$[0].ownerName").value("홍길동"))
            .andExpect(jsonPath("$[1].ownerName").value("김철수"))
            .andExpect(jsonPath("$[0].balance").value(10000))
            .andExpect(jsonPath("$[1].balance").value(20000));
    }
}