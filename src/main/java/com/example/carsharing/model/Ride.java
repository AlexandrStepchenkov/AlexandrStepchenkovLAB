package com.example.carsharing.model;

import lombok.Data;

@Data
public class Ride {
    private Long id;
    private Car car;
    private User user;
    private double distanceKm;
    private double durationHours;
    private String status;
}
