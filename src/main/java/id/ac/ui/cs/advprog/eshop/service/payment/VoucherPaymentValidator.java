package id.ac.ui.cs.advprog.eshop.service.payment;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VoucherPaymentValidator implements PaymentValidator {
    
    private static final String PAYMENT_METHOD = "VOUCHER";
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final int VOUCHER_LENGTH = 16;
    private static final int REQUIRED_DIGITS = 8;
    
    @Override
    public boolean validate(Map<String, String> paymentData) {
        String voucherCode = paymentData.get("voucherCode");
        
        if (voucherCode == null) {
            return false;
        }
        
        if (voucherCode.length() != VOUCHER_LENGTH) {
            return false;
        }
        
        if (!voucherCode.startsWith(VOUCHER_PREFIX)) {
            return false;
        }
        
        return countDigits(voucherCode) == REQUIRED_DIGITS;
    }
    
    @Override
    public String getPaymentMethod() {
        return PAYMENT_METHOD;
    }
    
    private long countDigits(String str) {
        return str.chars().filter(Character::isDigit).count();
    }
}

