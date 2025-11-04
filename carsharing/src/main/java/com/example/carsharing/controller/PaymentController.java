package com.example.carsharing.controller;

import com.example.carsharing.model.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final List<Payment> payments = new ArrayList<>();

    @GetMapping
    public List<Payment> getAllPayments() {
        return payments;
    }

    @PostMapping
    public Payment addPayment(@RequestBody Payment payment) {
        payments.add(payment);
        return payment;
    }
}
