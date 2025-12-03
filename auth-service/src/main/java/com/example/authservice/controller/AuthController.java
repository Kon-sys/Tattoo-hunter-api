package com.example.authservice.controller;


import com.example.authservice.dto.CountersDto;
import com.example.authservice.dto.JwtAuthenticationDto;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.UserInfoDto;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.model.User;
import com.example.authservice.security.JwtService;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


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
            if(request.getLogin() == null || request.getPassword() == null || request.getRole() == null) {
                return  ResponseEntity.badRequest().body("Заполните все поля");
            }

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

    @GetMapping("/by-login")
    public ResponseEntity<UserInfoDto> getUserByLogin(@RequestParam String login) {
        UserInfoDto userInfo = userService.getUserInfoByLogin(login);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh (@RequestBody JwtAuthenticationDto refreshToken) throws Exception {
        return jwtService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        // здесь можно добавить логику занесения токена в blacklist, если понадобится
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/counters")
    public ResponseEntity<CountersDto> getCounters() {
        CountersDto counters = userService.getCounters();
        return ResponseEntity.ok(counters);
    }
}