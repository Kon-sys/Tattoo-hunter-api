package com.example.authservice.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private Long id;          // id пользователя в auth-service
    private String login;
    private String role;      // "ROLE_EMPLOYEE" или "ROLE_COMPANY"

    // если у тебя есть прямые связи с Employee/Company — можно сразу отдать их id
    private Long employeeId;  // может быть null
    private Long companyId;   // может быть null
}
