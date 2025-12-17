package com.example.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkCategoriesPopularityDto {
    private List<WorkCategoryUsageDto> items;
    private long totalUsers;
}
