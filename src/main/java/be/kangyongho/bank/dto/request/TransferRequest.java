package be.kangyongho.bank.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransferRequest {

	@NotBlank(message = "출금 계좌번호는 필수입니다")
	private String fromAccountNumber;

	@NotBlank(message = "입금 계좌번호는 필수입니다")
	private String toAccountNumber;

	@NotNull(message = "이체금액은 필수입니다")
	@Positive(message = "이체금액은 0보다 커야 합니다")
	private BigDecimal amount;

	private String description;

	public TransferRequest(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String description) {
		this.fromAccountNumber = fromAccountNumber;
		this.toAccountNumber = toAccountNumber;
		this.amount = amount;
		this.description = description;
	}
}
