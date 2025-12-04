package com.example.listingvacanciesservice.mapper;

import com.example.listingvacanciesservice.dto.VacancyDto;
import com.example.listingvacanciesservice.model.Vacancy;
import com.example.listingvacanciesservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyMapper {

    private final CompanyService companyService;

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
            dto.setCompanyName(vacancy.getCompany().getName());
            dto.setCompanyId(vacancy.getCompany().getId()); // 🔥 вот это
        }

        return dto;
    }

    //VacancyDto → Vacancy
    public Vacancy toEntity(VacancyDto dto) {
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
        vacancy.setCompany(companyService.findByName(dto.getCompanyName()));
        return vacancy;
    }
}
