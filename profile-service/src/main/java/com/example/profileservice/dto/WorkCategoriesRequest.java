package com.example.profileservice.dto;


import com.example.profileservice.model.WorkCategory;
import lombok.Data;

import java.util.Set;

@Data
public class WorkCategoriesRequest {
    private Set<WorkCategory> workCategories;
}
