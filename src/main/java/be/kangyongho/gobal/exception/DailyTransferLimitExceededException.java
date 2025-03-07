package be.kangyongho.gobal.exception;

import java.math.BigDecimal;

public class DailyTransferLimitExceededException extends RuntimeException {
    public DailyTransferLimitExceededException(BigDecimal limit, BigDecimal attempted) {
        super(String.format("일일 이체 한도 %s원을 초과했습니다. 총 이체 시도액: %s원", 
            limit.toString(), attempted.toString()));
    }
}