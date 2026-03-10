package id.ac.ui.cs.advprog.eshop.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.eshop.model.Payment;

class PaymentRepositoryTest {
    PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
    }

    @Test
    void testSaveCreate() {
        Payment payment = new Payment("id-1", "VOUCHER", "SUCCESS", new HashMap<>());
        Payment result = paymentRepository.save(payment);
        Payment findResult = paymentRepository.findById("id-1");
        assertEquals(payment.getId(), result.getId());
        assertEquals(findResult.getId(), result.getId());
    }
}
