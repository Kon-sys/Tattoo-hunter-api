package com.example.listingvacanciesservice.dto;

import com.example.listingvacanciesservice.model.Busy;
import com.example.listingvacanciesservice.model.WorkSchedule;
import com.example.listingvacanciesservice.model.WorkType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyFilter {

    // "договорная" или число в строке ("5000")
    private String income;

    private List<Long> companyIds;

    private Busy busy;                 // точное совпадение
    private WorkSchedule workSchedule; // точное совпадение
    private WorkType workType;         // точное совпадение

    private Integer minExperience;     // опыт от ...
    private Integer maxExperience;     // опыт до ...

    private Integer minWorkingHours;   // часы работы от ...
    private Integer maxWorkingHours;   // часы работы до ...
}
