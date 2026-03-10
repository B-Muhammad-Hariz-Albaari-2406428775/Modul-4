package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
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
class OrderControllerTest {

    @Mock
    OrderService orderService;

    @Mock
    ProductService productService;

    @Mock
    PaymentService paymentService;

    @Mock
    Model model;

    @InjectMocks
    OrderController orderController;

    List<Product> products;
    Order order;

    @BeforeEach
    void setUp() {
        products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        products.add(product1);

        order = new Order("order-123", products, 1708560000L, "John Doe");
    }

    @Test
    void testCreateOrderPage() {
        when(productService.findAll()).thenReturn(products);

        String viewName = orderController.createOrderPage(model);

        assertEquals("createOrder", viewName);
        verify(productService, times(1)).findAll();
        verify(model, times(1)).addAttribute("products", products);
        verify(model, times(1)).addAttribute("author", "");
    }

    @Test
    void testCreateOrderPost() {
        List<String> productIds = Arrays.asList("eb558e9f-1c39-460e-8860-71af6af63bd6");
        when(productService.findById("eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(products.get(0));
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        String viewName = orderController.createOrderPost("John Doe", productIds, model);

        assertEquals("redirect:/order/history?author=John Doe", viewName);
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrderPostWithNoProducts() {
        when(productService.findAll()).thenReturn(products);

        String viewName = orderController.createOrderPost("John Doe", null, model);

        assertEquals("createOrder", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
        verify(orderService, times(0)).createOrder(any(Order.class));
    }

    @Test
    void testOrderHistoryPageWithAuthor() {
        List<Order> orders = Arrays.asList(order);
        when(orderService.findAllByAuthor("John Doe")).thenReturn(orders);

        String viewName = orderController.orderHistoryPage("John Doe", model);

        assertEquals("orderList", viewName);
        verify(orderService, times(1)).findAllByAuthor("John Doe");
        verify(model, times(1)).addAttribute("orders", orders);
        verify(model, times(1)).addAttribute("author", "John Doe");
    }

    @Test
    void testOrderHistoryPageWithoutAuthor() {
        String viewName = orderController.orderHistoryPage(null, model);

        assertEquals("orderHistory", viewName);
        verify(orderService, times(0)).findAllByAuthor(anyString());
    }

    @Test
    void testOrderHistoryPost() {
        String viewName = orderController.orderHistoryPost("John Doe", model);

        assertEquals("redirect:/order/history?author=John Doe", viewName);
    }

    @Test
    void testOrderHistoryPostWithEmptyAuthor() {
        String viewName = orderController.orderHistoryPost("", model);

        assertEquals("orderHistory", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testPayOrderPage() {
        when(orderService.findById("order-123")).thenReturn(order);

        String viewName = orderController.payOrderPage("order-123", model);

        assertEquals("payOrder", viewName);
        verify(orderService, times(1)).findById("order-123");
        verify(model, times(1)).addAttribute("order", order);
    }

    @Test
    void testPayOrderPageOrderNotFound() {
        when(orderService.findById("invalid-id")).thenReturn(null);

        String viewName = orderController.payOrderPage("invalid-id", model);

        assertEquals("orderHistory", viewName);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    void testPayOrderPostWithVoucher() {
        Map<String, String> allParams = new HashMap<>();
        allParams.put("paymentMethod", "VOUCHER");
        allParams.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = new Payment("payment-123", "VOUCHER", "SUCCESS", new HashMap<>());
        when(orderService.findById("order-123")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("VOUCHER"), anyMap())).thenReturn(payment);

        String viewName = orderController.payOrderPost("order-123", "VOUCHER", allParams, model);

        assertEquals("redirect:/payment/detail/payment-123", viewName);
        verify(paymentService, times(1)).addPayment(eq(order), eq("VOUCHER"), anyMap());
    }
}

