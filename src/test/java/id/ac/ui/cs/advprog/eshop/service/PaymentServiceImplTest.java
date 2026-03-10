package id.ac.ui.cs.advprog.eshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.payment.CashOnDeliveryPaymentValidator;
import id.ac.ui.cs.advprog.eshop.service.payment.PaymentValidatorFactory;
import id.ac.ui.cs.advprog.eshop.service.payment.VoucherPaymentValidator;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    private PaymentServiceImpl paymentService;
    private Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(2);
        products.add(product1);

        order = new Order("13652556-012a-4c07-b546-54eb1396d79b",
                products, 1708560000L, "Safira Sudrajat");

        // Create real validators and factory for testing
        VoucherPaymentValidator voucherValidator = new VoucherPaymentValidator();
        CashOnDeliveryPaymentValidator codValidator = new CashOnDeliveryPaymentValidator();
        List<id.ac.ui.cs.advprog.eshop.service.payment.PaymentValidator> validators = Arrays.asList(voucherValidator,
                codValidator);
        PaymentValidatorFactory validatorFactory = new PaymentValidatorFactory(validators);

        // Create service with real validators
        paymentService = new PaymentServiceImpl(paymentRepository, orderRepository, validatorFactory);
    }

    @Test
    void testAddPaymentVoucherSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");

        Payment expectedPayment = new Payment(order.getId(), "VOUCHER", "SUCCESS", data);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment result = paymentService.addPayment(order, "VOUCHER", data);

        assertEquals("SUCCESS", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testAddPaymentCODRejectedEmptyAddress() {
        Map<String, String> data = new HashMap<>();
        data.put("deliveryFee", "10000");

        Payment expectedPayment = new Payment(order.getId(), "CASH_ON_DELIVERY", "REJECTED", data);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment result = paymentService.addPayment(order, "CASH_ON_DELIVERY", data);

        assertEquals("REJECTED", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
