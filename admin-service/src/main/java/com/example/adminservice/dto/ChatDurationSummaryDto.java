package com.example.adminservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatDurationSummaryDto {

    private List<ChatDurationItemDto> items;
    private double avgDurationMinutes;
    private double medianDurationMinutes;
    private long maxDurationMinutes;

    public ChatDurationSummaryDto(List<ChatDurationItemDto> items,
                                  double avgDurationMinutes,
                                  double medianDurationMinutes,
                                  long maxDurationMinutes) {
        this.items = items;
        this.avgDurationMinutes = avgDurationMinutes;
        this.medianDurationMinutes = medianDurationMinutes;
        this.maxDurationMinutes = maxDurationMinutes;
    }
}
