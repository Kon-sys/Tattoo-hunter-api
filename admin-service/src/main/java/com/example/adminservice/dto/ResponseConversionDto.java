package com.example.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseConversionDto {
    private long total;
    private Map<String, Long> byStatus;
    private Double approvedPercent;
    private Double rejectedPercent;
    private Double avgProcessingHours;
}
