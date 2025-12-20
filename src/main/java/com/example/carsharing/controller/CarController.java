package com.example.carsharing.controller;

import com.example.carsharing.model.Car;
import com.example.carsharing.service.DB;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final DB db;

    @PostMapping("/add")
    public Car add(@RequestParam int carId,
                   @RequestParam String model,
                   @RequestParam String licensePlate,
                   @RequestParam double pricePerKm) {
        return db.saveCar(carId, model, licensePlate, pricePerKm);
    }

    @GetMapping
    public List<Car> all() {
        return db.cars();
    }

    @GetMapping("/{id}")
    public Car getById(@PathVariable int id) {
        return db.getCarById(id);
    }

    @GetMapping("/available")
    public List<Car> available() {
        return db.availableCars();
    }

    @GetMapping("/busy")
    public List<Car> busy() {
        return db.busyCars();
    }

    @GetMapping("/search")
    public List<Car> searchByModel(@RequestParam String model) {
        return db.findCarsByModel(model);
    }

    @PutMapping("/update")
    public Car update(@RequestParam int carId,
                      @RequestParam(required = false) String model,
                      @RequestParam(required = false) String licensePlate,
                      @RequestParam(required = false) Double pricePerKm,
                      @RequestParam(required = false) Boolean available) {
        return db.updateCar(carId, model, licensePlate, pricePerKm, available);
    }

    @PutMapping("/{id}/availability")
    public Car updateAvailability(@PathVariable int id, @RequestParam boolean available) {
        return db.updateCarAvailability(id, available);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam int carId) {
        db.deleteCar(carId);
        return "car disabled";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable int id) {
        db.deleteCar(id);
        return "Car with id " + id + " disabled";
    }
}