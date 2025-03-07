package be.kangyongho.bank.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.kangyongho.bank.dto.request.DepositRequest;
import be.kangyongho.bank.dto.request.TransferRequest;
import be.kangyongho.bank.dto.request.WithdrawRequest;
import be.kangyongho.bank.dto.response.TransactionDto;
import be.kangyongho.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "거래 API", description = "입금, 출금, 이체 기능 제공")
public class TransactionApiController {

	private final TransactionService transactionService;

	@PostMapping("/deposit")
	@Operation(summary = "입금", description = "계좌에 금액을 입금합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "입금 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	public ResponseEntity<TransactionDto> deposit(
		@Valid @RequestBody DepositRequest request) {
		TransactionDto transaction = transactionService.deposit(request);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	@PostMapping("/withdraw")
	@Operation(summary = "출금", description = "계좌에서 금액을 출금합니다. 일일 출금 한도는 1,000,000원입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "출금 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 또는 잔액 부족"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음"),
		@ApiResponse(responseCode = "422", description = "일일 출금 한도 초과")
	})
	public ResponseEntity<TransactionDto> withdraw(
		@Valid @RequestBody WithdrawRequest request) {
		TransactionDto transaction = transactionService.withdraw(request);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	@PostMapping("/transfer")
	@Operation(
		summary = "이체",
		description = "계좌에서 다른 계좌로 금액을 이체합니다. 이체 금액의 1%가 수수료로 부과되며, 일일 이체 한도는 3,000,000원입니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "이체 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 또는 잔액 부족"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음"),
		@ApiResponse(responseCode = "422", description = "일일 이체 한도 초과")
	})
	public ResponseEntity<TransactionDto> transfer(
		@Valid @RequestBody TransferRequest request) {
		TransactionDto transaction = transactionService.transfer(request);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	@GetMapping("/accounts/{accountNumber}")
	@Operation(summary = "계좌 거래내역 조회(페이징)", description = "계좌의 거래내역을 페이징하여 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	public ResponseEntity<Page<TransactionDto>> getAccountTransactions(
		@Parameter(description = "계좌번호", example = "1234567890")
		@PathVariable String accountNumber,
		@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		Page<TransactionDto> transactions = transactionService.getAccountTransactions(accountNumber, pageable);
		return ResponseEntity.ok(transactions);
	}

	@GetMapping("/accounts/{accountNumber}/all")
	@Operation(summary = "계좌 모든 거래내역 조회", description = "계좌의 모든 거래내역을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountNumber(
		@Parameter(description = "계좌번호", example = "1234567890")
		@PathVariable String accountNumber) {
		List<TransactionDto> transactions = transactionService.getAllTransactionsByAccountNumber(accountNumber);
		return ResponseEntity.ok(transactions);
	}
}