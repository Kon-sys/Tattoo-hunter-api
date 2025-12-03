package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountersDto {
    private long employeesCount;
    private long companiesCount;
    private long vacanciesCount;
}
