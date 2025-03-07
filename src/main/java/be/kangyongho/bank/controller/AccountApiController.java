package be.kangyongho.bank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.kangyongho.bank.dto.request.AccountCreateRequest;
import be.kangyongho.bank.dto.response.AccountDto;
import be.kangyongho.bank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "계좌 API", description = "계좌 생성 및 조회 기능 제공")
public class AccountApiController {

	private final AccountService accountService;

	@PostMapping
	@Operation(summary = "계좌 생성", description = "신규 계좌를 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "계좌 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	public ResponseEntity<AccountDto> createAccount(
		@Valid @RequestBody AccountCreateRequest request) {
		log.debug("test");
		AccountDto createdAccount = accountService.createAccount(request);
		return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
	}

	@GetMapping("/{accountNumber}")
	@Operation(summary = "계좌번호로 조회", description = "계좌번호로 계좌 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	public ResponseEntity<AccountDto> getAccountByNumber(
		@Parameter(description = "계좌번호", example = "1234567890")
		@PathVariable String accountNumber) {
		AccountDto account = accountService.getAccountByNumber(accountNumber);
		return ResponseEntity.ok(account);
	}

	@GetMapping
	@Operation(summary = "전체 계좌 조회", description = "모든 계좌 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<List<AccountDto>> getAllAccounts() {
		List<AccountDto> accounts = accountService.getAllAccounts();
		return ResponseEntity.ok(accounts);
	}
}