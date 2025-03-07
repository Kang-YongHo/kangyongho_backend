package be.kangyongho.bank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import be.kangyongho.bank.entity.Transaction;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionDto {
	private Long id;
	private Long sourceAccountId;
	private Long targetAccountId;
	private BigDecimal amount;
	private Transaction.TransactionType type;
	private String description;
	private LocalDateTime timestamp;

	@Builder
	public TransactionDto(Long id, Long sourceAccountId, Long targetAccountId, BigDecimal amount,
		Transaction.TransactionType type, String description, LocalDateTime timestamp) {
		this.id = id;
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.amount = amount;
		this.type = type;
		this.description = description;
		this.timestamp = timestamp;
	}

	public static TransactionDto fromEntity(Transaction transaction) {
		return TransactionDto.builder()
			.id(transaction.getId())
			.sourceAccountId(transaction.getSourceAccountId())
			.targetAccountId(transaction.getTargetAccountId())
			.amount(transaction.getAmount())
			.type(transaction.getType())
			.description(transaction.getDescription())
			.timestamp(transaction.getCreatedAt())
			.build();
	}
}
