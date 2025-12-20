package com.example.carsharing.controller;

import com.example.carsharing.model.User;
import com.example.carsharing.service.DB;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final DB db;

    @PostMapping("/add")
    public User add(@RequestParam int userId,
                    @RequestParam String name,
                    @RequestParam String email,
                    @RequestParam String phone) {
        return db.saveUser(userId, name, email, phone);
    }

    @GetMapping
    public List<User> all() {
        return db.users();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return db.getUserById(id);
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String email) {
        return db.findUsers(name, email);
    }

    @GetMapping("/email")
    public User getByEmail(@RequestParam String email) {
        return db.findUserByEmail(email);
    }

    @PutMapping("/update")
    public User update(@RequestParam int userId,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String phone) {
        return db.updateUser(userId, name, email, phone);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam int userId) {
        db.deleteUser(userId);
        return "deleted";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable int id) {
        db.deleteUser(id);
        return "User with id " + id + " deleted";
    }
}