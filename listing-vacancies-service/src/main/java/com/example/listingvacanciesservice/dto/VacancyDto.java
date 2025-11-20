package com.example.listingvacanciesservice.dto;


import com.example.listingvacanciesservice.model.Busy;
import com.example.listingvacanciesservice.model.WorkSchedule;
import com.example.listingvacanciesservice.model.WorkType;
import lombok.Data;

@Data
public class VacancyDto {
    private Long id;
    private String title;
    private String incomeLevel;
    private Busy busy;
    private int experience;
    private WorkSchedule workSchedule;
    private int workingHours;
    private WorkType workType;
    private String addInfo;
    private String listUrl;
    private String companyName;
}
