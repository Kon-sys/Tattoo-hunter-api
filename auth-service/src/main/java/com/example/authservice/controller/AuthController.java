package com.example.authservice.controller;


import com.example.authservice.dto.JwtAuthenticationDto;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.model.User;
import com.example.authservice.security.JwtService;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUserWithRole(
                    request.getLogin(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getRole()
            );

            JwtAuthenticationDto tokens = jwtService.generateAuthToken(
                    user.getLogin(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(tokens);

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("User with this login already exists");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody RegisterRequest request) {

        User user = userService.findByLogin(request.getLogin())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid login or password");
        }

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(
                user.getLogin(),
                user.getRole().name()
        );

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh (@RequestBody JwtAuthenticationDto refreshToken) throws Exception {
        return jwtService.refreshToken(refreshToken);
    }
}