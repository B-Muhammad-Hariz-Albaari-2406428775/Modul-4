package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;

public interface PaymentValidator {

    boolean validate(Map<String, String> paymentData);
    String getPaymentMethod();
}

