package com.example.adminservice.service;

import com.example.adminservice.dto.*;
import com.example.adminservice.model.ResponseApplication;
import com.example.adminservice.repo.ResponseApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseAnalyticsService {

    private final ResponseApplicationRepository repo;

    public ResponseConversionDto getResponsesConversion(Long companyId) {
        List<ResponseStatusCountDto> raw = repo.countByStatus(companyId);

        long total = raw.stream().mapToLong(ResponseStatusCountDto::getCount).sum();
        Map<String, Long> byStatus = raw.stream()
                .collect(Collectors.toMap(ResponseStatusCountDto::getStatus, ResponseStatusCountDto::getCount));

        long approved = byStatus.getOrDefault("APPROVED", 0L);
        long rejected = byStatus.getOrDefault("REJECTED", 0L);

        Double approvedPercent = total > 0 ? approved * 100.0 / total : null;
        Double rejectedPercent = total > 0 ? rejected * 100.0 / total : null;

        List<ResponseApplication> all = repo.findAllForAnalytics(companyId);

        long sumSec = 0;
        long processed = 0;
        for (ResponseApplication r : all) {
            if (r.getCreatedAt() != null && r.getUpdatedAt() != null && r.getStatus() != null) {
                if (!"PENDING".equalsIgnoreCase(r.getStatus())) {
                    long sec = Duration.between(r.getCreatedAt(), r.getUpdatedAt()).getSeconds();
                    if (sec >= 0) {
                        sumSec += sec;
                        processed++;
                    }
                }
            }
        }
        Double avgHours = processed > 0 ? (sumSec / 3600.0 / processed) : null;

        return new ResponseConversionDto(total, byStatus, approvedPercent, rejectedPercent, avgHours);
    }
}
