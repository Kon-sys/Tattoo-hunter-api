package com.example.chatservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // логин работника
    @Column(nullable = false)
    private String employeeLogin;

    // ID компании (из vacancy-service)
    @Column(nullable = false)
    private Long companyId;

    // ID вакансии, вокруг которой чат
    @Column(nullable = false)
    private Long vacancyId;

    private LocalDateTime createdAt;
}

