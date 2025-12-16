package com.example.adminservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkCategoriesPopularityDto {

    private long totalEmployeesWithCategories;
    private List<WorkCategoryUsageDto> items;

    public WorkCategoriesPopularityDto(long totalEmployeesWithCategories,
                                       List<WorkCategoryUsageDto> items) {
        this.totalEmployeesWithCategories = totalEmployeesWithCategories;
        this.items = items;
    }
}
