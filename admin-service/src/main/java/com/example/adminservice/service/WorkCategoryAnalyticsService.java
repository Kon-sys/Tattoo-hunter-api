package com.example.adminservice.service;

import com.example.adminservice.dto.WorkCategoriesPopularityDto;
import com.example.adminservice.dto.WorkCategoryUsageDto;
import com.example.adminservice.model.WorkCategory;
import com.example.adminservice.repo.UserWorkCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkCategoryAnalyticsService {

    private final UserWorkCategoryRepository userWorkCategoryRepository;

    public WorkCategoriesPopularityDto getCategoriesPopularity() {
        List<Object[]> raw = userWorkCategoryRepository.countEmployeesByCategoryRaw();

        long total = raw.stream()
                .mapToLong(r -> (Long) r[1])
                .sum();

        List<WorkCategoryUsageDto> items = raw.stream()
                .map(r -> {
                    WorkCategory category = (WorkCategory) r[0];
                    long count = (Long) r[1];
                    double percent = (total > 0)
                            ? count * 100.0 / total
                            : 0.0;
                    return new WorkCategoryUsageDto(category, count, percent);
                })
                .sorted(Comparator.comparingLong(WorkCategoryUsageDto::getEmployeesCount).reversed())
                .limit(5)
                .toList();

        return new WorkCategoriesPopularityDto(total, items);
    }
}
