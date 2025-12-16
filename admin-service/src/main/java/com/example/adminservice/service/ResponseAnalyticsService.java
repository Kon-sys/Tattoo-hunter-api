package com.example.adminservice.service;

import com.example.adminservice.dto.ResponseConversionDto;
import com.example.adminservice.dto.ResponseStatusCountDto;
import com.example.adminservice.model.ResponseApplication;
import com.example.adminservice.repo.ResponseApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseAnalyticsService {

    private final ResponseApplicationRepository responseRepository;

    public ResponseConversionDto getResponsesConversion() {

        // 1) Считаем по статусам
        List<ResponseStatusCountDto> raw = responseRepository.countByStatus();

        long total = raw.stream()
                .mapToLong(ResponseStatusCountDto::getCount)
                .sum();

        Map<String, Long> byStatus = raw.stream()
                .collect(Collectors.toMap(
                        ResponseStatusCountDto::getStatus,
                        ResponseStatusCountDto::getCount
                ));

        // 2) Расчет конверсии
        long approved = byStatus.getOrDefault("APPROVED", 0L);
        long rejected = byStatus.getOrDefault("REJECTED", 0L);

        Double approvedPercent = total > 0 ? (approved * 100.0 / total) : null;
        Double rejectedPercent = total > 0 ? (rejected * 100.0 / total) : null;

        // 3) Среднее время обработки заявки
        List<ResponseApplication> apps = responseRepository.findAll();

        long sumSec = 0;
        long countProcessed = 0;

        for (ResponseApplication r : apps) {
            if (r.getCreatedAt() != null && r.getUpdatedAt() != null) {
                long sec = Duration.between(r.getCreatedAt(), r.getUpdatedAt()).getSeconds();
                if (sec >= 0) {
                    sumSec += sec;
                    countProcessed++;
                }
            }
        }

        Double avgHours = countProcessed > 0
                ? (sumSec / 3600.0 / countProcessed)
                : null;

        return new ResponseConversionDto(total, byStatus, approvedPercent, rejectedPercent, avgHours);
    }
}
