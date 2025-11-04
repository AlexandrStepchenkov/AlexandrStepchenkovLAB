package com.example.carsharing.controller;

import com.example.carsharing.model.*;
import com.example.carsharing.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final List<Ride> rides = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final PaymentService paymentService;
    private long counter = 1;

    public RideController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Ride createRide(@RequestBody Ride ride) {
        ride.setId(counter++);
        ride.setStatus("CREATED");
        rides.add(ride);
        return ride;
    }

    @PutMapping("/{id}/start")
    public Ride startRide(@PathVariable Long id) {
        return updateStatus(id, "IN_PROGRESS");
    }

    @PutMapping("/{id}/complete")
    public Payment completeRide(@PathVariable Long id) {
        Ride ride = updateStatus(id, "COMPLETED");
        if (ride != null) {
            Payment payment = paymentService.calculatePayment(ride);
            payments.add(payment);
            return payment;
        }
        return null;
    }

    private Ride updateStatus(Long id, String newStatus) {
        return rides.stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst()
                .map(r -> {
                    r.setStatus(newStatus);
                    return r;
                })
                .orElse(null);
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rides;
    }
}