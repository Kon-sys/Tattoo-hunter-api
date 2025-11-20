package com.example.listingvacanciesservice.mapper;

import com.example.listingvacanciesservice.dto.CompanyDto;
import com.example.listingvacanciesservice.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setCity(company.getCity());
        return dto;
    }

    public Company toEntity(CompanyDto dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setAddress(dto.getAddress());
        company.setCity(dto.getCity());
        return company;
    }
}
