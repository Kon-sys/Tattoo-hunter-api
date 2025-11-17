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
        dto.setIncomeLevel(vacancy.getIncome_level());
        dto.setBusy(vacancy.getBusy());
        dto.setExperience(vacancy.getExperience());
        dto.setWorkSchedule(vacancy.getWorkSchedule());
        dto.setWorkingHours(vacancy.getWorking_hours());
        dto.setWorkType(vacancy.getWorkType());
        dto.setAddInfo(vacancy.getAddInfo());
        dto.setListUrl(vacancy.getList_url());

        if (vacancy.getCompany() != null) {
            dto.setCompanyId(vacancy.getCompany().getId());
        }

        return dto;
    }

    //VacancyDto → Vacancy
    public Vacancy toEntity(VacancyDto dto, Company company) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(dto.getTitle());
        vacancy.setIncome_level(dto.getIncomeLevel());
        vacancy.setBusy(dto.getBusy());
        vacancy.setExperience(dto.getExperience());
        vacancy.setWorkSchedule(dto.getWorkSchedule());
        vacancy.setWorking_hours(dto.getWorkingHours());
        vacancy.setWorkType(dto.getWorkType());
        vacancy.setAddInfo(dto.getAddInfo());
        vacancy.setList_url(dto.getListUrl());
        vacancy.setCompany(company);
        return vacancy;
    }
}
