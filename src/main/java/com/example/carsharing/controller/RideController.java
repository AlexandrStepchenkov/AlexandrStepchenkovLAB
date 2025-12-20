package com.example.carsharing.controller;

import com.example.carsharing.model.*;
import com.example.carsharing.service.DB;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {
    private final DB db;

    @PostMapping("/add")
    public Object add(@RequestParam int userId, @RequestParam int carId,
                      @RequestParam double dist, @RequestParam double hours) {
        Ride r = db.addRide(userId, carId, dist, hours);
        return r != null ? r : "car not available";
    }

    @GetMapping
    public List<Ride> all() {
        return db.rides();
    }

    @GetMapping("/{id}")
    public Ride getById(@PathVariable Long id) {
        return db.getRideById(id);
    }

    @GetMapping("/available-cars")
    public List<Car> availableCars() {
        return db.availableCars();
    }

    @GetMapping("/by-user")
    public List<Ride> byUser(@RequestParam int userId) {
        return db.ridesByUser(userId);
    }

    @GetMapping("/by-car")
    public List<Ride> byCar(@RequestParam int carId) {
        return db.ridesByCar(carId);
    }

    @GetMapping("/by-status")
    public List<Ride> byStatus(@RequestParam String status) {
        return db.ridesByStatus(status);
    }

    @GetMapping("/revenue")
    public double revenue() {
        return db.totalRevenue();
    }

    @GetMapping("/active-count")
    public int activeCount() {
        return db.activeRidesCount();
    }

    @GetMapping("/busy-cars")
    public List<Car> busyCars() {
        return db.busyCars();
    }

    @GetMapping("/stats/user/{userId}")
    public Map<String, Object> userStats(@PathVariable int userId) {
        return db.getUserRideStats(userId);
    }

    @GetMapping("/stats/car/{carId}")
    public Map<String, Object> carStats(@PathVariable int carId) {
        return db.getCarRideStats(carId);
    }

    @PutMapping("/start")
    public Object start(@RequestParam Long rideId) {
        Ride r = db.startRide(rideId);
        return r != null ? r : "cannot start";
    }

    @PutMapping("/complete")
    public Object complete(@RequestParam Long rideId) {
        Payment p = db.completeRide(rideId);
        return p != null ? p : "cannot complete";
    }

    @PutMapping("/{id}")
    public Ride updateRide(@PathVariable Long id,
                           @RequestParam(required = false) Double distanceKm,
                           @RequestParam(required = false) Double durationHours) {
        return db.updateRide(id, distanceKm, durationHours);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return db.deleteRide(id);
    }

    @DeleteMapping("/cancel")
    public Object cancel(@RequestParam Long rideId) {
        return db.cancelRide(rideId);
    }
}