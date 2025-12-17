package com.example.packet;
import org.springframework.web.bind.annotation.*;

@RestController
public class controller2 {
    @GetMapping("/bye")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("bb %s!", name);
    }
}
