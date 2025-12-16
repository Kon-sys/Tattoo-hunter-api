package com.example.adminservice.controller;

import com.example.adminservice.dto.ChatDurationSummaryDto;
import com.example.adminservice.service.ChatAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics/chats")
@RequiredArgsConstructor
public class ChatAnalyticsController {

    private final ChatAnalyticsService chatAnalyticsService;

    /**
     * GET /api/admin/analytics/chats/duration
     * Возвращает список чатов с длительностью переписки
     * + общую статистику (средняя, медиана, максимум).
     */
    @GetMapping("/duration")
    public ResponseEntity<ChatDurationSummaryDto> getChatDurations() {
        ChatDurationSummaryDto dto = chatAnalyticsService.getChatDurations();
        return ResponseEntity.ok(dto);
    }
}

