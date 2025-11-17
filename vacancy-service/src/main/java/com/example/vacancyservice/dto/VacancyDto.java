package com.example.vacancyservice.dto;


import com.example.vacancyservice.model.Busy;
import com.example.vacancyservice.model.WorkSchedule;
import com.example.vacancyservice.model.WorkType;
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
    private Long companyId;
}
