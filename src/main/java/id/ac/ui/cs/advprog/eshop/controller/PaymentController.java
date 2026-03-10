package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/detail")
    public String paymentDetailPage(Model model) {
        return "paymentDetailForm";
    }

    @PostMapping("/detail")
    public String paymentDetailPost(@RequestParam("paymentId") String paymentId, Model model) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a payment ID");
            return "paymentDetailForm";
        }
        return "redirect:/payment/detail/" + paymentId;
    }

    @GetMapping("/detail/{paymentId}")
    public String paymentDetailView(@PathVariable("paymentId") String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            model.addAttribute("error", "Payment not found");
            return "paymentDetailForm";
        }

        Order order = orderService.findById(payment.getId());
        model.addAttribute("payment", payment);
        model.addAttribute("order", order);
        return "paymentDetail";
    }

    @GetMapping("/admin/list")
    public String adminPaymentListPage(Model model) {
        List<Payment> allPayments = paymentService.getAllPayments();
        model.addAttribute("payments", allPayments);
        return "adminPaymentList";
    }

    @GetMapping("/admin/detail/{paymentId}")
    public String adminPaymentDetailPage(@PathVariable("paymentId") String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            model.addAttribute("error", "Payment not found");
            return "redirect:/payment/admin/list";
        }

        Order order = orderService.findById(payment.getId());
        model.addAttribute("payment", payment);
        model.addAttribute("order", order);
        return "adminPaymentDetail";
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String setPaymentStatus(@PathVariable("paymentId") String paymentId,
                                    @RequestParam("status") String status,
                                    Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            model.addAttribute("error", "Payment not found");
            return "redirect:/payment/admin/list";
        }

        try {
            paymentService.setStatus(payment, status);
            return "redirect:/payment/admin/detail/" + paymentId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid status");
            Order order = orderService.findById(payment.getId());
            model.addAttribute("payment", payment);
            model.addAttribute("order", order);
            return "adminPaymentDetail";
        }
    }
}

