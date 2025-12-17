package com.example.adminservice.service;

import com.example.adminservice.dto.*;
import com.example.adminservice.repo.UserWorkCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkCategoryAnalyticsService {

    private final UserWorkCategoryRepository repo;

    public WorkCategoriesPopularityDto getCategoriesPopularity() {
        List<Object[]> raw = repo.countByCategoryRaw();

        long total = raw.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();

        List<WorkCategoryUsageDto> items = raw.stream()
                .map(r -> {
                    String name = (String) r[0];
                    long count = ((Number) r[1]).longValue();
                    double percent = total > 0 ? count * 100.0 / total : 0.0;
                    return new WorkCategoryUsageDto(name, count, percent);
                })
                .sorted(Comparator.comparingLong(WorkCategoryUsageDto::getCount).reversed())
                .limit(5)
                .toList();

        return new WorkCategoriesPopularityDto(items, total);
    }
}
