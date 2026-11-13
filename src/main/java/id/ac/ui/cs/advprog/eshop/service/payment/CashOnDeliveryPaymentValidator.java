package id.ac.ui.cs.advprog.eshop.service.payment;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CashOnDeliveryPaymentValidator implements PaymentValidator {
    
    private static final String PAYMENT_METHOD = "CASH_ON_DELIVERY";
    
    @Override
    public boolean validate(Map<String, String> paymentData) {
        String address = paymentData.get("address");
        String deliveryFee = paymentData.get("deliveryFee");
        
        if (address == null || address.isEmpty()) {
            return false;
        }
        
        if (deliveryFee == null || deliveryFee.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return PAYMENT_METHOD;
    }
}

