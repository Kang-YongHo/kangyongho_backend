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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.kangyongho.bank.controller.TransactionApiController;
import be.kangyongho.bank.dto.request.DepositRequest;
import be.kangyongho.bank.dto.request.TransferRequest;
import be.kangyongho.bank.dto.request.WithdrawRequest;
import be.kangyongho.bank.dto.response.TransactionDto;
import be.kangyongho.bank.entity.Transaction;
import be.kangyongho.bank.service.TransactionService;
import be.kangyongho.gobal.exception.InsufficientBalanceException;

@WebMvcTest(TransactionApiController.class)
public class TransactionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionDto transactionDto;
    private DepositRequest depositRequest;
    private WithdrawRequest withdrawRequest;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        transactionDto = TransactionDto.builder()
            .id(1L)
            .sourceAccountId(1L)
            .targetAccountId(2L)
            .amount(BigDecimal.valueOf(1000))
            .type(Transaction.TransactionType.TRANSFER)
            .description("테스트 거래")
            .timestamp(now)
            .build();

        depositRequest = new DepositRequest("1234567890", BigDecimal.valueOf(1000), "입금 테스트");
        withdrawRequest = new WithdrawRequest("1234567890", BigDecimal.valueOf(1000), "출금 테스트");
        transferRequest = new TransferRequest(
            "1111111111", "2222222222", BigDecimal.valueOf(1000), "이체 테스트"
        );
    }

    @Test
    void depositSuccessTest() throws Exception {
        when(transactionService.deposit(any(DepositRequest.class))).thenReturn(transactionDto);

        mockMvc.perform(post("/api/v1/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(1000))
            .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    void withdrawSuccessTest() throws Exception {
        when(transactionService.withdraw(any(WithdrawRequest.class))).thenReturn(transactionDto);

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(1000))
            .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    void withdrawInsufficientBalanceTest() throws Exception {
        when(transactionService.withdraw(any(WithdrawRequest.class)))
            .thenThrow(new InsufficientBalanceException(BigDecimal.valueOf(500), BigDecimal.valueOf(1000)));

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void transferSuccessTest() throws Exception {
        when(transactionService.transfer(any(TransferRequest.class))).thenReturn(transactionDto);

        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(1000))
            .andExpect(jsonPath("$.type").value("TRANSFER"))
            .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    void getAccountTransactionsTest() throws Exception {
        List<TransactionDto> transactions = Arrays.asList(transactionDto);
        Page<TransactionDto> pagedTransactions = new PageImpl<>(transactions);

        when(transactionService.getAccountTransactions(any(String.class), any(Pageable.class)))
            .thenReturn(pagedTransactions);

        mockMvc.perform(get("/api/v1/transactions/accounts/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].amount").value(1000))
            .andExpect(jsonPath("$.content[0].type").value("TRANSFER"));
    }

    @Test
    void getAllTransactionsByAccountNumberTest() throws Exception {
        List<TransactionDto> transactions = Arrays.asList(transactionDto);

        when(transactionService.getAllTransactionsByAccountNumber("1234567890"))
            .thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions/accounts/1234567890/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].amount").value(1000))
            .andExpect(jsonPath("$[0].type").value("TRANSFER"));
    }
}