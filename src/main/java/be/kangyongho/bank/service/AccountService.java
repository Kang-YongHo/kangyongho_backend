package be.kangyongho.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.kangyongho.bank.dto.request.AccountCreateRequest;
import be.kangyongho.bank.dto.response.AccountDto;
import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.entity.Transaction;
import be.kangyongho.bank.repository.AccountRepository;
import be.kangyongho.bank.repository.TransactionRepository;
import be.kangyongho.gobal.exception.AccountNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final Random random = new Random();

	@Transactional
	public AccountDto createAccount(AccountCreateRequest request) {
		String accountNumber = generateUniqueAccountNumber();

		Account account = Account.builder()
			.accountNumber(accountNumber)
			.ownerName(request.getOwnerName())
			.balance(request.getInitialDeposit())
			.build();

		Account savedAccount = accountRepository.save(account);

		// 초기 입금 내역 기록
		if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
			Transaction transaction = Transaction.builder()
				.targetAccountId(savedAccount.getId())
				.amount(request.getInitialDeposit())
				.type(Transaction.TransactionType.DEPOSIT)
				.description("계좌 개설 초기 입금")
				.build();

			transactionRepository.save(transaction);
		}

		return AccountDto.fromEntity(savedAccount);
	}

	@Transactional(readOnly = true)
	public AccountDto getAccountByNumber(String accountNumber) {
		Account account = findAccountByAccountNumber(accountNumber);
		return AccountDto.fromEntity(account);
	}

	@Transactional(readOnly = true)
	public List<AccountDto> getAllAccounts() {
		return accountRepository.findAll().stream()
			.map(AccountDto::fromEntity)
			.toList();
	}

	// 내부 헬퍼 메서드
	private String generateUniqueAccountNumber() {
		String accountNumber;
		do {
			// 10자리 랜덤 숫자 생성
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				sb.append(random.nextInt(10));
			}
			accountNumber = sb.toString();
		} while (accountRepository.existsByAccountNumber(accountNumber));

		return accountNumber;
	}

	public Account findAccountByAccountNumber(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountNotFoundException(accountNumber, "accountNumber"));
	}

	public Account findAccountById(Long id) {
		return accountRepository.findById(id)
			.orElseThrow(() -> new AccountNotFoundException(id));
	}

	@Transactional
	public Account findAccountByAccountNumberWithLock(String accountNumber) {
		Account account = findAccountByAccountNumber(accountNumber);
		return accountRepository.lockAndFindById(account.getId())
			.orElseThrow(() -> new EntityNotFoundException("계좌를 찾을 수 없습니다: " + accountNumber));
	}
}