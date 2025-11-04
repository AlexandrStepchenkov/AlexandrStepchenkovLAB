package com.example.carsharing.model;

import lombok.Data;

@Data
public class Car {
    private Long id;
    private String model;
    private String licensePlate;
    private double pricePerKm;
    private boolean available = true;
}
