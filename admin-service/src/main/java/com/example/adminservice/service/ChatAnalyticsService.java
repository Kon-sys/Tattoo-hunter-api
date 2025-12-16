package com.example.adminservice.service;

import com.example.adminservice.dto.ChatDurationItemDto;
import com.example.adminservice.dto.ChatDurationSummaryDto;
import com.example.adminservice.repo.MessageAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAnalyticsService {

    private final MessageAnalyticsRepository messageAnalyticsRepository;

    public ChatDurationSummaryDto getChatDurations() {
        List<ChatDurationItemDto> items = messageAnalyticsRepository.findChatDurations();

        if (items.isEmpty()) {
            return new ChatDurationSummaryDto(List.of(), 0.0, 0.0, 0L);
        }

        // средняя
        double avg = items.stream()
                .mapToLong(ChatDurationItemDto::getDurationMinutes)
                .average()
                .orElse(0.0);

        // медиана
        List<Long> sorted = items.stream()
                .map(ChatDurationItemDto::getDurationMinutes)
                .sorted()
                .toList();

        int n = sorted.size();
        double median;
        if (n % 2 == 1) {
            median = sorted.get(n / 2);
        } else {
            median = (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        }

        long max = sorted.get(sorted.size() - 1);

        return new ChatDurationSummaryDto(items, avg, median, max);
    }
}

