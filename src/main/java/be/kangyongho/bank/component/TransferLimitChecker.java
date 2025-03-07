package be.kangyongho.bank.component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.entity.Transaction;
import be.kangyongho.bank.repository.TransactionRepository;
import be.kangyongho.gobal.exception.DailyTransferLimitExceededException;

@Component
public class TransferLimitChecker {
    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("3000000");
    
    private final TransactionRepository transactionRepository;
    
    @Autowired
    public TransferLimitChecker(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public void checkDailyTransferLimit(Account account, BigDecimal amount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        // 오늘 이체 내역 조회
        BigDecimal todayTotalTransfer = transactionRepository
            .findBySourceAccountIdAndTypeAndCreatedAtBetween(
                account.getId(), 
                Transaction.TransactionType.TRANSFER,
                startOfDay, 
                endOfDay
            )
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalTransfer = todayTotalTransfer.add(amount);
        
        if (totalTransfer.compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            throw new DailyTransferLimitExceededException(DAILY_TRANSFER_LIMIT, totalTransfer);
        }
    }
}