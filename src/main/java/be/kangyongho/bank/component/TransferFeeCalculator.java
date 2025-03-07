package be.kangyongho.bank.component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public class TransferFeeCalculator {
    private static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.01"); // 1% 수수료
    
    public BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(TRANSFER_FEE_RATE).setScale(0, RoundingMode.CEILING);
    }
    
    public BigDecimal calculateTotalWithFee(BigDecimal amount) {
        return amount.add(calculateFee(amount));
    }
}