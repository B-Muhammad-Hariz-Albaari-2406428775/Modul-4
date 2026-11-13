package id.ac.ui.cs.advprog.eshop.service.payment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentValidatorFactory {
    
    private final Map<String, PaymentValidator> validators;
    
    public PaymentValidatorFactory(List<PaymentValidator> validatorList) {
        this.validators = new HashMap<>();
        for (PaymentValidator validator : validatorList) {
            validators.put(validator.getPaymentMethod(), validator);
        }
    }

    public PaymentValidator getValidator(String paymentMethod) {
        return validators.get(paymentMethod);
    }
}

