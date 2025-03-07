package be.kangyongho.gobal.exception;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
	private final BigDecimal balance;
	private final BigDecimal requiredAmount;

	public InsufficientBalanceException(BigDecimal balance, BigDecimal requiredAmount) {
		super(String.format("잔액이 부족합니다. 현재 잔액: %s, 필요 금액: %s",
			balance.toString(), requiredAmount.toString()));
		this.balance = balance;
		this.requiredAmount = requiredAmount;
	}

	public InsufficientBalanceException(BigDecimal balance, BigDecimal requiredAmount, String message) {
		super(message);
		this.balance = balance;
		this.requiredAmount = requiredAmount;
	}

}