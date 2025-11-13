package com.example.authservice.dto;

import com.example.authservice.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String login;
    private String password;
    private Role role;
}