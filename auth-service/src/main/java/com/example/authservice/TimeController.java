package com.example.authservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class TimeController {

    @GetMapping("/api/time")
    public String currentTime() {
        return LocalDateTime.now().toString();
    }
}