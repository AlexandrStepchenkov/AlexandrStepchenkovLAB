package com.example.carsharing.service;

import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Ride;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final AtomicLong counter = new AtomicLong();

    public Payment calculatePayment(Ride ride) {
        if (ride == null) {
            throw new RuntimeException("Ride cannot be null");
        }

        if (ride.getCar() == null) {
            throw new RuntimeException("Car information is missing in ride");
        }

        Payment payment = new Payment();
        payment.setId(counter.incrementAndGet());

        // Расчет стоимости: расстояние × цена за км
        double pricePerKm = ride.getCar().getPricePerKm();
        if (pricePerKm <= 0) {
            throw new RuntimeException("Invalid price per km: " + pricePerKm);
        }

        double distance = ride.getDistanceKm();
        if (distance < 0) {
            throw new RuntimeException("Invalid distance: " + distance);
        }

        double amount = distance * pricePerKm;
        payment.setAmount(Math.round(amount * 100.0) / 100.0); // Округление до 2 знаков

        payment.setRide(ride);
        payment.setStatus("COMPLETED");

        return payment;
    }
}