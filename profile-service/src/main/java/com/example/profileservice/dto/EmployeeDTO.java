package com.example.profileservice.dto;

import com.example.profileservice.model.Gender;
import com.example.profileservice.model.WorkCategory;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String fatherName;
    private LocalDate birthDate;
    private Gender gender;
    private String city;
    private int experience;
    private String phone;
    private String email;
    private String telegram;
    private Set<WorkCategory> workCategories;
    private String addInfo;
    private String mainPhoto;
    private String resume;
}
