package com.example.adminservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "response_application", schema = "public")
@Getter
@Setter
public class ResponseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "vacancy_id")
    private Long vacancyId;

    @Column(name = "employee_login")
    private String employeeLogin;

    @Column(name = "status")
    private String status; // можно заменить на enum, если есть общий ResponseStatus

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // getters/setters
}
