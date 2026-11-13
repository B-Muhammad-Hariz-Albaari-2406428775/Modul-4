package id.ac.ui.cs.advprog.eshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

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

        VoucherPaymentValidator voucherValidator = new VoucherPaymentValidator();
        CashOnDeliveryPaymentValidator codValidator = new CashOnDeliveryPaymentValidator();
        List<id.ac.ui.cs.advprog.eshop.service.payment.PaymentValidator> validators =
                Arrays.asList(voucherValidator, codValidator);
        PaymentValidatorFactory validatorFactory = new PaymentValidatorFactory(validators);

        paymentService = new PaymentServiceImpl(paymentRepository, orderRepository, validatorFactory);
    }

    // -------------------------------------------------------------------------
    // PaymentServiceImpl — addPayment / determinePaymentStatus
    // -------------------------------------------------------------------------

    @Test
    void testAddPaymentVoucherSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "VOUCHER", data);

        assertEquals("SUCCESS", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testAddPaymentVoucherRejectedInvalidCode() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "INVALID");
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "VOUCHER", data);

        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    void testAddPaymentCODSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("address", "Jl. Merdeka No. 1");
        data.put("deliveryFee", "15000");
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "CASH_ON_DELIVERY", data);

        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void testAddPaymentCODRejectedEmptyAddress() {
        Map<String, String> data = new HashMap<>();
        data.put("deliveryFee", "10000");
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "CASH_ON_DELIVERY", data);

        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    void testAddPaymentCODRejectedEmptyDeliveryFee() {
        Map<String, String> data = new HashMap<>();
        data.put("address", "Jl. Merdeka No. 1");
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "CASH_ON_DELIVERY", data);

        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    void testAddPaymentUnknownMethodIsRejected() {
        // Covers the validator == null branch in determinePaymentStatus
        Map<String, String> data = new HashMap<>();
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.addPayment(order, "BITCOIN", data);

        assertEquals("REJECTED", result.getStatus());
    }

    // -------------------------------------------------------------------------
    // PaymentServiceImpl — setStatus
    // -------------------------------------------------------------------------

    @Test
    void testSetStatusSuccessUpdatesOrderToSuccess() {
        Payment payment = new Payment(order.getId(), "VOUCHER", "SUCCESS", new HashMap<>());
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.setStatus(payment, "SUCCESS");

        assertEquals("SUCCESS", result.getStatus());
        assertEquals("SUCCESS", order.getStatus());
    }

    @Test
    void testSetStatusRejectedUpdatesOrderToFailed() {
        Payment payment = new Payment(order.getId(), "VOUCHER", "SUCCESS", new HashMap<>());
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.setStatus(payment, "REJECTED");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void testSetStatusOrderNotFoundThrows() {
        Payment payment = new Payment("non-existent-id", "VOUCHER", "SUCCESS", new HashMap<>());
        when(orderRepository.findById("non-existent-id")).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> paymentService.setStatus(payment, "SUCCESS"));
    }

    @Test
    void testSetStatusOtherStatusDoesNotChangeOrder() {
        // A status other than SUCCESS/REJECTED should leave order status unchanged
        String originalOrderStatus = order.getStatus();
        Payment payment = new Payment(order.getId(), "VOUCHER", "WAITING_PAYMENT", new HashMap<>());
        when(orderRepository.findById(order.getId())).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        paymentService.setStatus(payment, "WAITING_PAYMENT");

        assertEquals(originalOrderStatus, order.getStatus());
    }

    // -------------------------------------------------------------------------
    // PaymentServiceImpl — getPayment / getAllPayments
    // -------------------------------------------------------------------------

    @Test
    void testGetPaymentReturnsCorrectPayment() {
        Payment payment = new Payment(order.getId(), "VOUCHER", "SUCCESS", new HashMap<>());
        when(paymentRepository.findById(order.getId())).thenReturn(payment);

        Payment result = paymentService.getPayment(order.getId());

        assertEquals(payment, result);
    }

    @Test
    void testGetAllPaymentsReturnsAll() {
        Payment p1 = new Payment(order.getId(), "VOUCHER", "SUCCESS", new HashMap<>());
        Payment p2 = new Payment("other-id", "CASH_ON_DELIVERY", "REJECTED", new HashMap<>());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Payment> results = paymentService.getAllPayments();

        assertEquals(2, results.size());
    }

    @Test
    void testGetAllPaymentsReturnsEmptyList() {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Payment> results = paymentService.getAllPayments();

        assertTrue(results.isEmpty());
    }

    // -------------------------------------------------------------------------
    // VoucherPaymentValidator — all branches
    // -------------------------------------------------------------------------

    @Test
    void testVoucherValidatorNullCode() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        Map<String, String> data = new HashMap<>();
        // no voucherCode key → null
        assertFalse(validator.validate(data));
    }

    @Test
    void testVoucherValidatorWrongLength() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP123");          // only 8 chars
        assertFalse(validator.validate(data));
    }

    @Test
    void testVoucherValidatorWrongPrefix() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "STORE1234ABC5678"); // 16 chars, wrong prefix
        assertFalse(validator.validate(data));
    }

    @Test
    void testVoucherValidatorNotEnoughDigits() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOPABCDEFGHIJK"); // 16 chars, correct prefix, 0 digits
        assertFalse(validator.validate(data));
    }

    @Test
    void testVoucherValidatorExactlyEightDigitsSuccess() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678"); // 16 chars, ESHOP prefix, 8 digits
        assertTrue(validator.validate(data));
    }

    @Test
    void testVoucherValidatorGetPaymentMethod() {
        VoucherPaymentValidator validator = new VoucherPaymentValidator();
        assertEquals("VOUCHER", validator.getPaymentMethod());
    }

    // -------------------------------------------------------------------------
    // CashOnDeliveryPaymentValidator — all branches
    // -------------------------------------------------------------------------

    @Test
    void testCODValidatorNullAddress() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("deliveryFee", "10000");
        // no address key → null
        assertFalse(validator.validate(data));
    }

    @Test
    void testCODValidatorEmptyAddress() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("address", "");
        data.put("deliveryFee", "10000");
        assertFalse(validator.validate(data));
    }

    @Test
    void testCODValidatorNullDeliveryFee() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("address", "Jl. Merdeka No. 1");
        // no deliveryFee key → null
        assertFalse(validator.validate(data));
    }

    @Test
    void testCODValidatorEmptyDeliveryFee() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("address", "Jl. Merdeka No. 1");
        data.put("deliveryFee", "");
        assertFalse(validator.validate(data));
    }

    @Test
    void testCODValidatorSuccess() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        Map<String, String> data = new HashMap<>();
        data.put("address", "Jl. Merdeka No. 1");
        data.put("deliveryFee", "15000");
        assertTrue(validator.validate(data));
    }

    @Test
    void testCODValidatorGetPaymentMethod() {
        CashOnDeliveryPaymentValidator validator = new CashOnDeliveryPaymentValidator();
        assertEquals("CASH_ON_DELIVERY", validator.getPaymentMethod());
    }
}