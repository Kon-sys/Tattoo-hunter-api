package com.example.vacancyservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    private String incomeLevel;

    @Enumerated(EnumType.STRING)
    private Busy busy;

    private int experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkSchedule workSchedule;

    private int workingHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    private String addInfo;

    private String listUrl;
}

