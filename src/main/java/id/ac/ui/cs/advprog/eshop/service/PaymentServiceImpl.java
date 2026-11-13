package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.payment.PaymentValidator;
import id.ac.ui.cs.advprog.eshop.service.payment.PaymentValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentValidatorFactory validatorFactory;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentValidatorFactory validatorFactory) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.validatorFactory = validatorFactory;
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String status = determinePaymentStatus(method, paymentData);
        Payment payment = new Payment(order.getId(), method, status, paymentData);
        
        return setStatus(payment, status);
    }

    private String determinePaymentStatus(String method, Map<String, String> paymentData) {
        PaymentValidator validator = validatorFactory.getValidator(method);

        if (validator == null) {
            return PaymentStatus.REJECTED.name();
        }

        return validator.validate(paymentData) ? PaymentStatus.SUCCESS.name() : PaymentStatus.REJECTED.name();
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        Order order = orderRepository.findById(payment.getId());

        if (order == null) {
            throw new NoSuchElementException("Order not found with ID: " + payment.getId());
        }

        if (status.equals(PaymentStatus.SUCCESS.name())) {
            order.setStatus(OrderStatus.SUCCESS.name());
        } else if (status.equals(PaymentStatus.REJECTED.name())) {
            order.setStatus(OrderStatus.FAILED.name());
        }

        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}