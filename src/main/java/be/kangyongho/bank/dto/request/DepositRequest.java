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
public class DepositRequest {

	@NotBlank(message = "계좌번호는 필수입니다")
	private String accountNumber;

	@NotNull(message = "입금액은 필수입니다")
	@Positive(message = "입금액은 0보다 커야 합니다")
	private BigDecimal amount;

	private String description;

	public DepositRequest(String accountNumber, BigDecimal amount, String description) {
		this.accountNumber = accountNumber;
		this.amount = amount;
		this.description = description;
	}
}
