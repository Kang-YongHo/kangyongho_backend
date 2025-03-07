package be.kangyongho.bank.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import be.kangyongho.gobal.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final AccountService accountService;
	private final WithdrawalLimitChecker withdrawalLimitChecker;
	private final TransferLimitChecker transferLimitChecker;
	private final TransferFeeCalculator transferFeeCalculator;

	@Transactional
	public TransactionDto deposit(DepositRequest request) {
		Account account = accountService.findAccountByAccountNumberWithLock(request.getAccountNumber());

		account.deposit(request.getAmount());
		accountRepository.save(account);

		Transaction transaction = Transaction.builder()
			.targetAccountId(account.getId())
			.amount(request.getAmount())
			.type(Transaction.TransactionType.DEPOSIT)
			.description(request.getDescription() != null ? request.getDescription() : "입금")
			.build();

		Transaction savedTransaction = transactionRepository.save(transaction);
		return TransactionDto.fromEntity(savedTransaction);
	}

	@Transactional
	public TransactionDto withdraw(WithdrawRequest request) {
		Account account = accountService.findAccountByAccountNumberWithLock(request.getAccountNumber());

		if (!account.hasEnoughBalance(request.getAmount())) {
			throw new InsufficientBalanceException(account.getBalance(), request.getAmount());
		}

		withdrawalLimitChecker.checkDailyWithdrawalLimit(account, request.getAmount());

		account.withdraw(request.getAmount());
		accountRepository.save(account);

		Transaction transaction = Transaction.builder()
			.sourceAccountId(account.getId())
			.amount(request.getAmount())
			.type(Transaction.TransactionType.WITHDRAW)
			.description(request.getDescription() != null ? request.getDescription() : "출금")
			.build();

		Transaction savedTransaction = transactionRepository.save(transaction);
		return TransactionDto.fromEntity(savedTransaction);
	}

	@Transactional
	public TransactionDto transfer(TransferRequest request) {
		if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
			throw new IllegalArgumentException("출금 계좌와 입금 계좌가 동일합니다.");
		}

		Account sourceAccount = accountService.findAccountByAccountNumberWithLock(request.getFromAccountNumber());
		Account targetAccount = accountService.findAccountByAccountNumberWithLock(request.getToAccountNumber());

		transferLimitChecker.checkDailyTransferLimit(sourceAccount, request.getAmount());

		BigDecimal fee = transferFeeCalculator.calculateFee(request.getAmount());
		BigDecimal totalAmount = transferFeeCalculator.calculateTotalWithFee(request.getAmount());

		if (!sourceAccount.hasEnoughBalance(totalAmount)) {
			throw new InsufficientBalanceException(sourceAccount.getBalance(), totalAmount,
				"잔액이 부족합니다. 필요 금액: " + totalAmount + " (송금액: " + request.getAmount() + ", 수수료: " + fee + ")");
		}

		sourceAccount.withdraw(request.getAmount());
		targetAccount.deposit(request.getAmount());

		accountRepository.save(sourceAccount);
		accountRepository.save(targetAccount);

		Transaction transaction = Transaction.builder()
			.sourceAccountId(sourceAccount.getId())
			.targetAccountId(targetAccount.getId())
			.amount(request.getAmount())
			.type(Transaction.TransactionType.TRANSFER)
			.fee(fee)
			.description(request.getDescription() != null ? request.getDescription() :
				sourceAccount.getOwnerName() + "님으로부터 " + targetAccount.getOwnerName() + "님에게 이체")
			.build();

		Transaction savedTransaction = transactionRepository.save(transaction);
		return TransactionDto.fromEntity(savedTransaction);
	}

	@Transactional(readOnly = true)
	public Page<TransactionDto> getAccountTransactions(String accountNumber, Pageable pageable) {
		Account account = accountService.findAccountByAccountNumberWithLock(accountNumber);

		return transactionRepository.findAllByAccountId(account.getId(), pageable)
			.map(TransactionDto::fromEntity);
	}

	@Transactional(readOnly = true)
	public List<TransactionDto> getAllTransactionsByAccountNumber(String accountNumber) {
		Account account = accountService.findAccountByAccountNumberWithLock(accountNumber);
		Long accountId = account.getId();

		return transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(accountId, accountId)
			.stream()
			.map(TransactionDto::fromEntity)
			.toList();
	}
}
