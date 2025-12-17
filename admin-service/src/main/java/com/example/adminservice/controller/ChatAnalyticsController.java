package com.example.adminservice.controller;

import com.example.adminservice.dto.ChatDurationSummaryDto;
import com.example.adminservice.dto.ChatTimeSeriesDto;
import com.example.adminservice.service.ChatAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/analytics/chats")
@RequiredArgsConstructor
public class ChatAnalyticsController {

    private final ChatAnalyticsService service;

    @GetMapping("/duration")
    public ResponseEntity<ChatDurationSummaryDto> duration(
            @RequestParam(required = false) Long companyId
    ) {
        return ResponseEntity.ok(service.getChatDurations(companyId));
    }

    @GetMapping("/timeseries")
    public ResponseEntity<ChatTimeSeriesDto> timeseries(
            @RequestParam String granularity,                 // DAY|MONTH|YEAR
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "14") int horizon,
            @RequestParam(defaultValue = "60") int lookback
    ) {
        return ResponseEntity.ok(service.getChatTimeSeries(granularity, companyId, from, to, horizon, lookback));
    }
}
