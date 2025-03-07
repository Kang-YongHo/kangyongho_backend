package be.kangyongho.bank.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountCreateRequest {

	@NotBlank(message = "계좌 소유자 이름은 필수입니다")
	private String ownerName;

	@NotNull(message = "초기 입금액은 필수입니다")
	@PositiveOrZero(message = "초기 입금액은 0 이상이어야 합니다")
	private BigDecimal initialDeposit;

	public AccountCreateRequest(String ownerName, BigDecimal initialDeposit) {
		this.ownerName = ownerName;
		this.initialDeposit = initialDeposit;
	}
}

