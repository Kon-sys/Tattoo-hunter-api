package com.example.listingvacanciesservice.dto;

import lombok.Data;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String address;
    private String city;
}
