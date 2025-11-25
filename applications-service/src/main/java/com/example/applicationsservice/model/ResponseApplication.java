package com.example.applicationsservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ResponseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // вакансия из listingvacanciesservice
    private Long vacancyId;

    // компания, на чью вакансию отклик
    private Long companyId;

    // логин работника, который откликнулся
    private String employeeLogin;

    @Enumerated(EnumType.STRING)
    private ResponseStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

