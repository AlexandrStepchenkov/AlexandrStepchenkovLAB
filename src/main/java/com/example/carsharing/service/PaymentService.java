package com.example.carsharing.service;

import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Ride;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final AtomicLong counter = new AtomicLong();

    public Payment calculatePayment(Ride ride) {
        Payment payment = new Payment();
        payment.setId(counter.incrementAndGet());
        double amount = ride.getDistanceKm() * ride.getCar().getPricePerKm();
        payment.setAmount(amount);
        payment.setRide(ride);
        return payment;
    }
}
