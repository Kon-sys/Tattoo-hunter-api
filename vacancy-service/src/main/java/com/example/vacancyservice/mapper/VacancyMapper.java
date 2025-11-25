package com.example.vacancyservice.mapper;


import com.example.vacancyservice.dto.VacancyDto;
import com.example.vacancyservice.model.Company;
import com.example.vacancyservice.model.Vacancy;
import org.springframework.stereotype.Component;

@Component
public class VacancyMapper {

    //Vacancy → VacancyDto
    public VacancyDto toDto(Vacancy vacancy) {
        VacancyDto dto = new VacancyDto();
        dto.setId(vacancy.getId());
        dto.setTitle(vacancy.getTitle());
        dto.setIncomeLevel(vacancy.getIncomeLevel());
        dto.setBusy(vacancy.getBusy());
        dto.setExperience(vacancy.getExperience());
        dto.setWorkSchedule(vacancy.getWorkSchedule());
        dto.setWorkingHours(vacancy.getWorkingHours());
        dto.setWorkType(vacancy.getWorkType());
        dto.setAddInfo(vacancy.getAddInfo());
        dto.setListUrl(vacancy.getListUrl());

        if (vacancy.getCompany() != null) {
            dto.setCompanyId(vacancy.getCompany().getId());
        }

        return dto;
    }

    //VacancyDto → Vacancy
    public Vacancy toEntity(VacancyDto dto, Company company) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(dto.getTitle());
        vacancy.setIncomeLevel(dto.getIncomeLevel());
        vacancy.setBusy(dto.getBusy());
        vacancy.setExperience(dto.getExperience());
        vacancy.setWorkSchedule(dto.getWorkSchedule());
        vacancy.setWorkingHours(dto.getWorkingHours());
        vacancy.setWorkType(dto.getWorkType());
        vacancy.setAddInfo(dto.getAddInfo());
        vacancy.setListUrl(dto.getListUrl());
        vacancy.setCompany(company);
        return vacancy;
    }
}
