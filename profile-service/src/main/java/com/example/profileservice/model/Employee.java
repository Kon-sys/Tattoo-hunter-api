package com.example.profileservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    private String firstName;

    private String lastName;

    private String fatherName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String city;

    private Integer experience;

    private String phone;

    private String email;

    private String telegram;

    @ElementCollection(targetClass = WorkCategory.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_work_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "work_category")
    private Set<WorkCategory> workCategories = new HashSet<>();

    private String addInfo;

    private String mainPhoto;

    private String resume;
}
