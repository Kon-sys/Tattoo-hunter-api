package com.example.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkCategoryUsageDto {
    private String name;
    private long count;
    private double percent;
}
