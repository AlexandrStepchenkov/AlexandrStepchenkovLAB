package com.example.packet;
import org.springframework.web.bind.annotation.*;

@RestController
public class controller {
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
