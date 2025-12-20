package com.example.carsharing.controller;

import com.example.carsharing.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public static final List<User> users = new ArrayList<>();
    private long counter = 1;

    @PostMapping
    public User addUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone) {

        User user = new User();
        user.setId(counter++);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);

        users.add(user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static User getUserById(Long id) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        return users.stream()
                .filter(u -> {
                    boolean matches = true;
                    if (name != null) {
                        matches = matches && u.getName().toLowerCase().contains(name.toLowerCase());
                    }
                    if (email != null) {
                        matches = matches && u.getEmail().toLowerCase().contains(email.toLowerCase());
                    }
                    return matches;
                })
                .toList();
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        return users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst()
                .map(u -> {
                    if (name != null) u.setName(name);
                    if (email != null) u.setEmail(email);
                    if (phone != null) u.setPhone(phone);
                    return u;
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> userToRemove = users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst();

        if (userToRemove.isPresent()) {
            users.remove(userToRemove.get());
            return "User with id " + id + " deleted";
        }
        return "User not found";
    }
}