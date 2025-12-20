package com.example.carsharing.controller;

import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Ride;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final List<Payment> payments = new ArrayList<>();
    private long counter = 1;

    @GetMapping
    public List<Payment> getAllPayments() {
        return payments;
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return payments.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/search")
    public List<Payment> searchPayments(
            @RequestParam(required = false) Long rideId,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String status) {

        return payments.stream()
                .filter(p -> {
                    boolean matches = true;
                    if (rideId != null) {
                        matches = matches && p.getRide() != null &&
                                Objects.equals(p.getRide().getId(), rideId);
                    }
                    if (minAmount != null) {
                        matches = matches && p.getAmount() >= minAmount;
                    }
                    if (maxAmount != null) {
                        matches = matches && p.getAmount() <= maxAmount;
                    }
                    if (status != null) {
                        matches = matches && p.getStatus() != null &&
                                p.getStatus().equalsIgnoreCase(status);
                    }
                    return matches;
                })
                .toList();
    }

    @PostMapping
    public Payment addPayment(
            @RequestParam Long rideId,
            @RequestParam double amount,
            @RequestParam(defaultValue = "PENDING") String status) {

        // Получаем реальную поездку
        Ride ride = RideController.getRideById(rideId);
        if (ride == null) {
            throw new RuntimeException("Ride with id " + rideId + " not found");
        }

        Payment payment = new Payment();
        payment.setId(counter++);
        payment.setRide(ride);  // Используем реальный объект
        payment.setAmount(amount);
        payment.setStatus(status);

        payments.add(payment);
        return payment;
    }

    @PutMapping("/{id}")
    public Payment updatePayment(
            @PathVariable Long id,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status) {

        return payments.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .map(p -> {
                    if (amount != null) p.setAmount(amount);
                    if (status != null) p.setStatus(status);
                    return p;
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deletePayment(@PathVariable Long id) {
        Optional<Payment> paymentToRemove = payments.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();

        if (paymentToRemove.isPresent()) {
            payments.remove(paymentToRemove.get());
            return "Payment with id " + id + " deleted";
        }
        return "Payment not found";
    }
}