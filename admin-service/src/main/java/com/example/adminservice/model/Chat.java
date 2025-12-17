package com.example.adminservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Getter
@Setter
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "employee_login", nullable = false)
    private String employeeLogin;

    @Column(name = "vacancy_id", nullable = false)
    private Long vacancyId;
}
