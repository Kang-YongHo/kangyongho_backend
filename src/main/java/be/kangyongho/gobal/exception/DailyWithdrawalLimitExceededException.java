package be.kangyongho.gobal.exception;

import java.math.BigDecimal;

public class DailyWithdrawalLimitExceededException extends RuntimeException {
    public DailyWithdrawalLimitExceededException(BigDecimal limit, BigDecimal attempted) {
        super(String.format("일일 출금 한도 %s원을 초과했습니다. 총 출금 시도액: %s원", 
            limit.toString(), attempted.toString()));
    }
}