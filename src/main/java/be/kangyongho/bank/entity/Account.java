package be.kangyongho.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "account")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", unique = true, nullable = false)
	private String accountNumber;

	@Column(name = "owner_name", nullable = false)
	private String ownerName;

	@Column(nullable = false)
	private BigDecimal balance;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Account(Long id, String accountNumber, String ownerName, BigDecimal balance, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.ownerName = ownerName;
		this.balance = balance;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public void deposit(BigDecimal amount) {
		this.balance = this.balance.add(amount);
	}

	public void withdraw(BigDecimal amount) {
		this.balance = this.balance.subtract(amount);
	}

	public boolean hasEnoughBalance(BigDecimal amount) {
		return this.balance.compareTo(amount) >= 0;
	}
}
