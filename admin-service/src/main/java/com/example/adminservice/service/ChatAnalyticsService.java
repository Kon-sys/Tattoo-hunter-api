package com.example.adminservice.service;

import com.example.adminservice.dto.*;
import com.example.adminservice.repo.MessageAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatAnalyticsService {

    private final MessageAnalyticsRepository repo;
    private final ForecastService forecastService;

    public ChatDurationSummaryDto getChatDurations(Long companyId) {
        // ✅ берём уже TOP-5 и сразу с именами (native query)
        List<Object[]> rows = repo.findTop5ChatDurationsRaw(companyId);

        List<ChatDurationItemDto> items = new ArrayList<>();
        for (Object[] r : rows) {
            // порядок колонок должен совпадать с запросом:
            // 0 chat_id
            // 1 company_name
            // 2 employee_first_name
            // 3 employee_last_name
            // 4 duration_minutes (double)
            // 5 messages_count (long)

            Long chatId = ((Number) r[0]).longValue();
            String companyName = (String) r[1];
            String firstName = (String) r[2];
            String lastName = (String) r[3];

            long durationMinutes = Math.round(((Number) r[4]).doubleValue());
            long messagesCount = ((Number) r[5]).longValue();

            ChatDurationItemDto dto = new ChatDurationItemDto(null, null, null,null, null, null, null);
            dto.setChatId(chatId);
            dto.setDurationMinutes(durationMinutes);
            dto.setMessagesCount(messagesCount);

            dto.setCompanyName(companyName);
            dto.setEmployeeFirstName(firstName);
            dto.setEmployeeLastName(lastName);

            items.add(dto);
        }

        if (items.isEmpty()) {
            return new ChatDurationSummaryDto(items, 0, 0, 0);
        }

        double avg = items.stream().mapToLong(ChatDurationItemDto::getDurationMinutes).average().orElse(0.0);

        List<Long> sorted = items.stream()
                .map(ChatDurationItemDto::getDurationMinutes)
                .sorted()
                .toList();

        int n = sorted.size();
        double median = (n % 2 == 1)
                ? sorted.get(n / 2)
                : (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;

        long max = sorted.get(sorted.size() - 1);

        return new ChatDurationSummaryDto(items, avg, median, max);
    }


    public ChatTimeSeriesDto getChatTimeSeries(
            String granularity, Long companyId, LocalDate from, LocalDate to, int horizon, int lookback
    ) {
        String g = (granularity == null ? "DAY" : granularity.toUpperCase(Locale.ROOT));

        // включаем весь день "to"
        LocalDateTime fromDt = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toDt = (to == null) ? null : to.plusDays(1).atStartOfDay();

        List<Object[]> raw = switch (g) {
            case "MONTH" -> repo.messageCountByMonth(companyId, fromDt, toDt);
            case "YEAR"  -> repo.messageCountByYear(companyId, fromDt, toDt);
            default      -> repo.messageCountByDay(companyId, fromDt, toDt);
        };

        // raw -> map(period -> value)
        Map<String, Long> map = new HashMap<>();
        for (Object[] r : raw) {
            String period = String.valueOf(r[0]);
            long value = ((Number) r[1]).longValue();
            map.put(period, value);
        }

        // 1) Собираем "красивый" полный ряд периодов (с нулями)
        List<String> periods = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        if (from != null && to != null && !from.isAfter(to)) {
            // если пользователь задал период — заполняем строго его
            switch (g) {
                case "MONTH" -> {
                    LocalDate cur = from.withDayOfMonth(1);
                    LocalDate end = to.withDayOfMonth(1);
                    while (!cur.isAfter(end)) {
                        String key = String.format("%04d-%02d", cur.getYear(), cur.getMonthValue()); // YYYY-MM
                        periods.add(key);
                        values.add(map.getOrDefault(key, 0L));
                        cur = cur.plusMonths(1);
                    }
                }
                case "YEAR" -> {
                    int y = from.getYear();
                    int endY = to.getYear();
                    for (int year = y; year <= endY; year++) {
                        String key = String.valueOf(year); // YYYY
                        periods.add(key);
                        values.add(map.getOrDefault(key, 0L));
                    }
                }
                default -> { // DAY
                    LocalDate cur = from;
                    while (!cur.isAfter(to)) {
                        String key = cur.toString(); // YYYY-MM-DD
                        periods.add(key);
                        values.add(map.getOrDefault(key, 0L));
                        cur = cur.plusDays(1);
                    }
                }
            }
        } else {
            // если фильтр дат не задан — оставляем как есть (как раньше)
            // но сортируем, чтобы lastPeriod был корректным
            List<String> sortedKeys = new ArrayList<>(map.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                periods.add(key);
                values.add(map.getOrDefault(key, 0L));
            }
        }

        // points (факт)
        List<TimeSeriesPointDto> points = new ArrayList<>();
        for (int i = 0; i < periods.size(); i++) {
            points.add(new TimeSeriesPointDto(periods.get(i), values.get(i), false));
        }

        // 2) Forecast
        List<Long> history = values;
        if (lookback > 0 && history.size() > lookback) {
            history = history.subList(history.size() - lookback, history.size());
        }

        List<Long> forecast = forecastService.linearTrendForecast(history, horizon);

        if (!periods.isEmpty() && !forecast.isEmpty()) {
            String last = periods.get(periods.size() - 1);
            List<String> nextPeriods = buildNextPeriods(g, last, forecast.size());
            for (int i = 0; i < forecast.size(); i++) {
                points.add(new TimeSeriesPointDto(nextPeriods.get(i), forecast.get(i), true));
            }
        }

        return new ChatTimeSeriesDto(g, companyId, points);
    }


    private List<String> buildNextPeriods(String granularity, String last, int n) {
        List<String> out = new ArrayList<>();
        switch (granularity) {
            case "YEAR" -> {
                int y = Integer.parseInt(last);
                for (int i = 1; i <= n; i++) out.add(String.valueOf(y + i));
            }
            case "MONTH" -> {
                int y = Integer.parseInt(last.substring(0, 4));
                int m = Integer.parseInt(last.substring(5, 7));
                for (int i = 1; i <= n; i++) {
                    int mm = m + i;
                    int yy = y + (mm - 1) / 12;
                    int m2 = ((mm - 1) % 12) + 1;
                    out.add(String.format("%04d-%02d", yy, m2));
                }
            }
            default -> {
                LocalDate d = LocalDate.parse(last);
                for (int i = 1; i <= n; i++) out.add(d.plusDays(i).toString());
            }
        }
        return out;
    }
}
