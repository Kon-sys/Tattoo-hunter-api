package com.example.adminservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseConversionDto {

    private long totalResponses;
    private Map<String, Long> byStatus;

    private Double approvedConversionPercent;  // APPROVED %
    private Double rejectedPercent;             // REJECTED %

    private Double avgProcessingHours;          // среднее время обработки заявки

    public ResponseConversionDto(long totalResponses,
                                 Map<String, Long> byStatus,
                                 Double approvedConversionPercent,
                                 Double rejectedPercent,
                                 Double avgProcessingHours) {
        this.totalResponses = totalResponses;
        this.byStatus = byStatus;
        this.approvedConversionPercent = approvedConversionPercent;
        this.rejectedPercent = rejectedPercent;
        this.avgProcessingHours = avgProcessingHours;
    }
}
