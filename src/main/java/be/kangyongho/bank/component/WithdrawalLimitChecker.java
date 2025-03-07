package be.kangyongho.bank.component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.entity.Transaction;
import be.kangyongho.bank.repository.TransactionRepository;
import be.kangyongho.gobal.exception.DailyWithdrawalLimitExceededException;

@Component
public class WithdrawalLimitChecker {
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("1000000");
    
    private final TransactionRepository transactionRepository;
    
    @Autowired
    public WithdrawalLimitChecker(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public void checkDailyWithdrawalLimit(Account account, BigDecimal amount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        BigDecimal todayWithdrawals = transactionRepository
            .findBySourceAccountIdAndTypeAndCreatedAtBetween(
                account.getId(), 
                Transaction.TransactionType.WITHDRAW,
                startOfDay, 
                endOfDay
            )
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalWithdrawal = todayWithdrawals.add(amount);
        
        if (totalWithdrawal.compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new DailyWithdrawalLimitExceededException(DAILY_WITHDRAWAL_LIMIT, totalWithdrawal);
        }
    }
}