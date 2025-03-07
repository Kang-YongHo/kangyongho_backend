package be.kangyongho.bank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import be.kangyongho.bank.entity.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDto {
	private Long id;
	private String accountNumber;
	private String ownerName;
	private BigDecimal balance;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	public AccountDto(Long id, String accountNumber, String ownerName, BigDecimal balance, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.ownerName = ownerName;
		this.balance = balance;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static AccountDto fromEntity(Account account) {
		return AccountDto.builder()
			.id(account.getId())
			.accountNumber(account.getAccountNumber())
			.ownerName(account.getOwnerName())
			.balance(account.getBalance())
			.createdAt(account.getCreatedAt())
			.updatedAt(account.getUpdatedAt())
			.build();
	}
}
