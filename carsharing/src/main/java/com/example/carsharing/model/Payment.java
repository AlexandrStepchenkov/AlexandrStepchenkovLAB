package com.example.carsharing.model;

import lombok.Data;

@Data
public class Payment {
    private Long id;
    private Ride ride;
    private double amount;
}
