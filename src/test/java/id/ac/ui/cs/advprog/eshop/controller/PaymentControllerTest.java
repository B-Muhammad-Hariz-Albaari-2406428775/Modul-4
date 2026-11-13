package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    PaymentService paymentService;

    @Mock
    OrderService orderService;

    @Mock
    Model model;

    @InjectMocks
    PaymentController paymentController;

    Payment payment;
    Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        products.add(product1);

        order = new Order("order-123", products, 1708560000L, "John Doe");
        payment = new Payment("payment-123", "VOUCHER", "SUCCESS", new HashMap<>());
    }

    @Test
    void testPaymentDetailPage() {
        String viewName = paymentController.paymentDetailPage(model);

        assertEquals("paymentDetailForm", viewName);
    }

    @Test
    void testPaymentDetailPost() {
        String viewName = paymentController.paymentDetailPost("payment-123", model);

        assertEquals("redirect:/payment/detail/payment-123", viewName);
    }

    @Test
    void testPaymentDetailPostWithEmptyId() {
        String viewName = paymentController.paymentDetailPost("", model);

        assertEquals("paymentDetailForm", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testPaymentDetailView() {
        when(paymentService.getPayment("payment-123")).thenReturn(payment);
        when(orderService.findById("payment-123")).thenReturn(order);

        String viewName = paymentController.paymentDetailView("payment-123", model);

        assertEquals("paymentDetail", viewName);
        verify(paymentService, times(1)).getPayment("payment-123");
        verify(orderService, times(1)).findById("payment-123");
        verify(model, times(1)).addAttribute("payment", payment);
        verify(model, times(1)).addAttribute("order", order);
    }

    @Test
    void testPaymentDetailViewPaymentNotFound() {
        when(paymentService.getPayment("invalid-id")).thenReturn(null);

        String viewName = paymentController.paymentDetailView("invalid-id", model);

        assertEquals("paymentDetailForm", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testAdminPaymentListPage() {
        List<Payment> payments = Arrays.asList(payment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        String viewName = paymentController.adminPaymentListPage(model);

        assertEquals("adminPaymentList", viewName);
        verify(paymentService, times(1)).getAllPayments();
        verify(model, times(1)).addAttribute("payments", payments);
    }

    @Test
    void testAdminPaymentDetailPage() {
        when(paymentService.getPayment("payment-123")).thenReturn(payment);
        when(orderService.findById("payment-123")).thenReturn(order);

        String viewName = paymentController.adminPaymentDetailPage("payment-123", model);

        assertEquals("adminPaymentDetail", viewName);
        verify(paymentService, times(1)).getPayment("payment-123");
        verify(orderService, times(1)).findById("payment-123");
        verify(model, times(1)).addAttribute("payment", payment);
        verify(model, times(1)).addAttribute("order", order);
    }

    @Test
    void testAdminPaymentDetailPagePaymentNotFound() {
        when(paymentService.getPayment("invalid-id")).thenReturn(null);

        String viewName = paymentController.adminPaymentDetailPage("invalid-id", model);

        assertEquals("redirect:/payment/admin/list", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testSetPaymentStatus() {
        when(paymentService.getPayment("payment-123")).thenReturn(payment);
        when(paymentService.setStatus(payment, "SUCCESS")).thenReturn(payment);

        String viewName = paymentController.setPaymentStatus("payment-123", "SUCCESS", model);

        assertEquals("redirect:/payment/admin/detail/payment-123", viewName);
        verify(paymentService, times(1)).getPayment("payment-123");
        verify(paymentService, times(1)).setStatus(payment, "SUCCESS");
    }

    @Test
    void testSetPaymentStatusPaymentNotFound() {
        when(paymentService.getPayment("invalid-id")).thenReturn(null);

        String viewName = paymentController.setPaymentStatus("invalid-id", "SUCCESS", model);

        assertEquals("redirect:/payment/admin/list", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testSetPaymentStatusInvalidStatus() {
        when(paymentService.getPayment("payment-123")).thenReturn(payment);
        when(paymentService.setStatus(payment, "INVALID")).thenThrow(new IllegalArgumentException());
        when(orderService.findById("payment-123")).thenReturn(order);

        String viewName = paymentController.setPaymentStatus("payment-123", "INVALID", model);

        assertEquals("adminPaymentDetail", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testPaymentDetailPostWithNullId() {
        String viewName = paymentController.paymentDetailPost(null, model);

        assertEquals("paymentDetailForm", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }
}

