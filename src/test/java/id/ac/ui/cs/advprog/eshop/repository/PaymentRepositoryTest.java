package id.ac.ui.cs.advprog.eshop.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;

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

    @Test
    void testFindAllIfEmpty() {
        List<Payment> payments = paymentRepository.findAll();
        assertTrue(payments.isEmpty());
    }

    @Test
    void testFindAllIfNotEmpty() {
        HashMap<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment1 = new Payment("id-1", "VOUCHER", "SUCCESS", paymentData);
        paymentRepository.save(payment1);
        
        Payment payment2 = new Payment("id-2", "CASH_ON_DELIVERY", "SUCCESS", paymentData);
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(2, payments.size()); // Sesuai pola tutorial di halaman 100 
        assertEquals("id-1", payments.get(0).getId());
        assertEquals("id-2", payments.get(1).getId());
    }
}
