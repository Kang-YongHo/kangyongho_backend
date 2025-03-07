package be.kangyongho.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "transactions")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "source_account_id")
	private Long sourceAccountId;

	@Column(name = "target_account_id")
	private Long targetAccountId;

	@Column(nullable = false)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type;

	@Column(nullable = false)
	private String description;

	@Column(nullable = true)
	private BigDecimal fee;

	@CreationTimestamp
	@Column(name = "createdAt", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public Transaction(Long id, Long sourceAccountId, Long targetAccountId, BigDecimal amount, TransactionType type,
		String description, BigDecimal fee, LocalDateTime createdAt) {
		this.id = id;
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.amount = amount;
		this.type = type;
		this.description = description;
		this.fee = fee;
		this.createdAt = createdAt;
	}

	public enum TransactionType {
		DEPOSIT, WITHDRAW, TRANSFER
	}
}
