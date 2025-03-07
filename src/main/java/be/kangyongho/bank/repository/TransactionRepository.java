package be.kangyongho.bank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.kangyongho.bank.entity.Account;
import be.kangyongho.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query("SELECT t FROM Transaction t WHERE t.sourceAccountId = :accountId OR t.targetAccountId = :accountId ORDER BY t.createdAt DESC")
	Page<Transaction> findAllByAccountId(@Param("accountId") Long accountId, Pageable pageable);

	List<Transaction> findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(Long sourceAccountId, Long targetAccountId);
	List<Transaction> findBySourceAccountIdAndTypeAndCreatedAtBetween(
		Long sourceAccountId,
		Transaction.TransactionType type,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);
}
