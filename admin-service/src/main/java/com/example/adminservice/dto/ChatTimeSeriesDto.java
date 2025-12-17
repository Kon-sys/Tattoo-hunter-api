package com.example.adminservice.dto;

import java.util.List;

public record ChatTimeSeriesDto(
        String granularity,
        Long companyId,
        List<TimeSeriesPointDto> points
) {}
