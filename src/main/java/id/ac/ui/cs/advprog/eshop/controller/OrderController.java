package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        List<Product> allProducts = productService.findAll();
        model.addAttribute("products", allProducts);
        model.addAttribute("author", "");
        return "createOrder";
    }

    @PostMapping("/create")
    public String createOrderPost(@RequestParam("author") String author,
            @RequestParam(value = "productIds", required = false) List<String> productIds,
            Model model) {
        if (productIds == null || productIds.isEmpty() || author == null || author.trim().isEmpty()) {
            model.addAttribute("error", "Please select at least one product and provide your name");
            List<Product> allProducts = productService.findAll();
            model.addAttribute("products", allProducts);
            model.addAttribute("author", author);
            return "createOrder";
        }

        List<Product> selectedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productService.findById(productId);
            if (product != null) {
                selectedProducts.add(product);
            }
        }

        if (selectedProducts.isEmpty()) {
            model.addAttribute("error", "No valid products selected");
            List<Product> allProducts = productService.findAll();
            model.addAttribute("products", allProducts);
            model.addAttribute("author", author);
            return "createOrder";
        }

        String orderId = UUID.randomUUID().toString();
        Long orderTime = System.currentTimeMillis() / 1000;
        Order order = new Order(orderId, selectedProducts, orderTime, author);
        orderService.createOrder(order);

        return "redirect:/order/history?author=" + author;
    }

    @GetMapping("/history")
    public String orderHistoryPage(@RequestParam(value = "author", required = false) String author, Model model) {
        if (author != null && !author.trim().isEmpty()) {
            List<Order> orders = orderService.findAllByAuthor(author);
            model.addAttribute("orders", orders);
            model.addAttribute("author", author);
            return "orderList";
        }
        return "orderHistory";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam("author") String author, Model model) {
        if (author == null || author.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your name");
            return "orderHistory";
        }
        return "redirect:/order/history?author=" + author;
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable("orderId") String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "orderHistory";
        }
        model.addAttribute("order", order);
        return "payOrder";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrderPost(@PathVariable("orderId") String orderId,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam Map<String, String> allParams,
            Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "orderHistory";
        }

        // Extract payment data from request parameters
        Map<String, String> paymentData = new HashMap<>();
        if ("VOUCHER".equals(paymentMethod)) {
            String voucherCode = allParams.get("voucherCode");
            if (voucherCode != null) {
                paymentData.put("voucherCode", voucherCode);
            }
        } else if ("CASH_ON_DELIVERY".equals(paymentMethod)) {
            String address = allParams.get("address");
            String deliveryFee = allParams.get("deliveryFee");
            if (address != null) {
                paymentData.put("address", address);
            }
            if (deliveryFee != null) {
                paymentData.put("deliveryFee", deliveryFee);
            }
        }

        Payment payment = paymentService.addPayment(order, paymentMethod, paymentData);
        return "redirect:/payment/detail/" + payment.getId();
    }
}
