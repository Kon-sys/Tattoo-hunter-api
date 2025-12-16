package com.example.adminservice.dto;

import com.example.adminservice.model.WorkCategory;
import lombok.Data;

@Data
public class WorkCategoryUsageDto {

    private WorkCategory category;
    private long employeesCount;
    private double percentOfAll;

    public WorkCategoryUsageDto(WorkCategory category,
                                long employeesCount,
                                double percentOfAll) {
        this.category = category;
        this.employeesCount = employeesCount;
        this.percentOfAll = percentOfAll;
    }
}
