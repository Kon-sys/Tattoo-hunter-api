package com.example.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatDurationSummaryDto {
    private List<ChatDurationItemDto> items;
    private double avgMinutes;
    private double medianMinutes;
    private long maxMinutes;
}
