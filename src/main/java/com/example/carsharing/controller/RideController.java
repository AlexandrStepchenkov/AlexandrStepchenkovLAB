package com.example.carsharing.controller;

import com.example.carsharing.model.*;
import com.example.carsharing.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    public static final List<Ride> rides = new ArrayList<>();
    private final PaymentService paymentService;
    private long counter = 1;

    public RideController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Ride createRide(
            @RequestParam Long carId,
            @RequestParam Long userId,
            @RequestParam double distanceKm,
            @RequestParam double durationHours) {

        Car car = CarController.getCarById(carId);
        if (car == null) {
            throw new RuntimeException("Car with id " + carId + " not found");
        }

        User user = UserController.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User with id " + userId + " not found");
        }

        if (!car.isAvailable()) {
            throw new RuntimeException("Car with id " + carId + " is not available");
        }

        Ride ride = new Ride();
        ride.setId(counter++);
        ride.setCar(car);
        ride.setUser(user);
        ride.setDistanceKm(distanceKm);
        ride.setDurationHours(durationHours);
        ride.setStatus("CREATED");

        car.setAvailable(false);

        rides.add(ride);
        return ride;
    }

    public static Ride getRideById(Long id) {
        return rides.stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static List<Ride> getAllRidesStatic() {
        return new ArrayList<>(rides);
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rides;
    }

    @GetMapping("/{id}")
    public Ride getRide(@PathVariable Long id) {
        return getRideById(id);
    }

    @GetMapping("/search")
    public List<Ride> searchRides(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long carId) {

        return rides.stream()
                .filter(r -> {
                    boolean matches = true;
                    if (status != null) {
                        matches = matches && r.getStatus() != null &&
                                r.getStatus().equalsIgnoreCase(status);
                    }
                    if (userId != null) {
                        matches = matches && r.getUser() != null &&
                                Objects.equals(r.getUser().getId(), userId);
                    }
                    if (carId != null) {
                        matches = matches && r.getCar() != null &&
                                Objects.equals(r.getCar().getId(), carId);
                    }
                    return matches;
                })
                .toList();
    }

    @PutMapping("/{id}")
    public Ride updateRide(
            @PathVariable Long id,
            @RequestParam(required = false) Double distanceKm,
            @RequestParam(required = false) Double durationHours,
            @RequestParam(required = false) String status) {

        return rides.stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst()
                .map(r -> {
                    if (distanceKm != null) r.setDistanceKm(distanceKm);
                    if (durationHours != null) r.setDurationHours(durationHours);
                    if (status != null) r.setStatus(status);
                    return r;
                })
                .orElse(null);
    }

    @PutMapping("/{id}/start")
    public Ride startRide(@PathVariable Long id) {
        return updateStatus(id, "IN_PROGRESS");
    }

    @PutMapping("/{id}/complete")
    public Payment completeRide(@PathVariable Long id) {
        Ride ride = updateStatus(id, "COMPLETED");
        if (ride != null) {
            if (ride.getCar() != null) {
                ride.getCar().setAvailable(true);
            }
            return paymentService.calculatePayment(ride);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteRide(@PathVariable Long id) {
        Optional<Ride> rideToRemove = rides.stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst();

        if (rideToRemove.isPresent()) {
            Ride ride = rideToRemove.get();
            if (ride.getCar() != null) {
                ride.getCar().setAvailable(true);
            }
            rides.remove(ride);
            return "Ride with id " + id + " deleted";
        }
        return "Ride not found";
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
}