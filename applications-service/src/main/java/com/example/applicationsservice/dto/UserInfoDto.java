package com.example.applicationsservice.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private Long id;
    private String login;
    private String role;      // "ROLE_EMPLOYEE" / "ROLE_COMPANY" и т.п.

}

