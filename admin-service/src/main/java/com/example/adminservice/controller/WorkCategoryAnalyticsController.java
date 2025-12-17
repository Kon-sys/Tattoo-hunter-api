package com.example.adminservice.controller;

import com.example.adminservice.dto.WorkCategoriesPopularityDto;
import com.example.adminservice.service.WorkCategoryAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics/categories")
@RequiredArgsConstructor
public class WorkCategoryAnalyticsController {

    private final WorkCategoryAnalyticsService service;

    @GetMapping("/popularity")
    public ResponseEntity<WorkCategoriesPopularityDto> popularity() {
        return ResponseEntity.ok(service.getCategoriesPopularity());
    }
}
