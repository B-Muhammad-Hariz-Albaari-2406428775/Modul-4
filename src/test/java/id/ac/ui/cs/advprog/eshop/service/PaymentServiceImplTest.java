package id.ac.ui.cs.advprog.eshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.eshop.model.Payment;

public class PaymentServiceImplTest {
    @Test
    void testAddPaymentVoucherSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment result = paymentService.addPayment(order, "VOUCHER", data);
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void testAddPaymentCODRejectedEmptyAddress() {
        Map<String, String> data = new HashMap<>();
        data.put("deliveryFee", "10000");
        Payment result = paymentService.addPayment(order, "CASH_ON_DELIVERY", data);
        assertEquals("REJECTED", result.getStatus());
    }
}
