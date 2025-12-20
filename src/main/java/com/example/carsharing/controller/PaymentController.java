package com.example.carsharing.controller;

import com.example.carsharing.model.Payment;
import com.example.carsharing.service.DB;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final DB db;

    @GetMapping
    public List<Payment> all() {
        return db.payments();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Long id) {
        return db.getPaymentById(id);
    }

    @GetMapping("/ride/{rideId}")
    public Payment getByRideId(@PathVariable Long rideId) {
        return db.getPaymentByRideId(rideId);
    }

    @GetMapping("/search")
    public List<Payment> search(@RequestParam(required = false) Double minAmount,
                                @RequestParam(required = false) Double maxAmount) {
        return db.findPaymentsByAmount(minAmount, maxAmount);
    }

    @GetMapping("/total")
    public double totalRevenue() {
        return db.totalRevenue();
    }

    @GetMapping("/stats/monthly")
    public List<Map<String, Object>> monthlyStats() {
        return db.getMonthlyRevenue();
    }

    @PostMapping("/add")
    public Payment add(@RequestParam Long rideId,
                       @RequestParam double amount) {
        return db.createManualPayment(rideId, amount);
    }

    @PutMapping("/{id}")
    public Payment update(@PathVariable Long id,
                          @RequestParam(required = false) Double amount) {
        return db.updatePayment(id, amount);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return db.deletePayment(id);
    }
}