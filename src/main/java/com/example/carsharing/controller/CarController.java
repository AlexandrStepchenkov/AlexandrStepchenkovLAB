package com.example.carsharing.controller;

import com.example.carsharing.model.Car;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    public static final List<Car> cars = new ArrayList<>();
    private long counter = 1;

    @PostMapping
    public Car addCar(
            @RequestParam String model,
            @RequestParam String licensePlate,
            @RequestParam double pricePerKm,
            @RequestParam(defaultValue = "true") boolean available) {

        Car car = new Car();
        car.setId(counter++);
        car.setModel(model);
        car.setLicensePlate(licensePlate);
        car.setPricePerKm(pricePerKm);
        car.setAvailable(available);

        cars.add(car);
        return car;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return cars;
    }

    @GetMapping("/{id}")
    public Car getCar(@PathVariable Long id) {
        return cars.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static Car getCarById(Long id) {
        return cars.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/search")
    public List<Car> searchCars(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Boolean available) {

        return cars.stream()
                .filter(c -> {
                    boolean matches = true;
                    if (model != null) {
                        matches = matches && c.getModel().toLowerCase().contains(model.toLowerCase());
                    }
                    if (available != null) {
                        matches = matches && c.isAvailable() == available;
                    }
                    return matches;
                })
                .toList();
    }

    @PutMapping("/{id}")
    public Car updateCar(
            @PathVariable Long id,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) Double pricePerKm,
            @RequestParam(required = false) Boolean available) {

        return cars.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .map(c -> {
                    if (model != null) c.setModel(model);
                    if (licensePlate != null) c.setLicensePlate(licensePlate);
                    if (pricePerKm != null) c.setPricePerKm(pricePerKm);
                    if (available != null) c.setAvailable(available);
                    return c;
                })
                .orElse(null);
    }

    @PutMapping("/{id}/availability")
    public Car updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return cars.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .map(c -> {
                    c.setAvailable(available);
                    return c;
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteCar(@PathVariable Long id) {
        Optional<Car> carToRemove = cars.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst();

        if (carToRemove.isPresent()) {
            cars.remove(carToRemove.get());
            return "Car with id " + id + " deleted";
        }
        return "Car not found";
    }
}