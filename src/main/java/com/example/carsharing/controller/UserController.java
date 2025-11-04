package com.example.carsharing.controller;

import com.example.carsharing.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final List<User> users = new ArrayList<>();
    private long counter = 1;

    @PostMapping
    public User addUser(@RequestBody User user) {
        user.setId(counter++);
        users.add(user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }
}
