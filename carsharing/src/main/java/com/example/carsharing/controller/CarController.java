package com.example.carsharing.controller;

import com.example.carsharing.model.Car;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final List<Car> cars = new ArrayList<>();
    private long counter = 1;

    @PostMapping
    public Car addCar(@RequestBody Car car) {
        car.setId(counter++);
        cars.add(car);
        return car;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return cars;
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
}
