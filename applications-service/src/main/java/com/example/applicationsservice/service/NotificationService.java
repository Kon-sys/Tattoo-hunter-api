package com.example.applicationsservice.service;

import com.example.applicationsservice.model.ResponseApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final StringRedisTemplate redisTemplate;

    private String companyResponsesKey(Long companyId) {
        return "company:" + companyId + ":responses";
    }

    /**
     * Добавить id отклика в Redis-список компании
     */
    public void addResponseForCompany(ResponseApplication app) {
        String listKey = companyResponsesKey(app.getCompanyId());
        try {
            redisTemplate.opsForList().leftPush(listKey, app.getId().toString());
        } catch (DataAccessException e) {
            log.warn("Redis unavailable in addResponseForCompany, skip cache for responseId={}", app.getId(), e);
        } catch (Exception e) {
            log.warn("Unexpected error in addResponseForCompany, responseId={}", app.getId(), e);
        }
    }

    /**
     * Получить список ID откликов компании из Redis.
     * Если Redis пуст / недоступен — возвращаем пустой список.
     */
    public List<Long> getCompanyResponseIds(Long companyId) {
        String listKey = companyResponsesKey(companyId);
        try {
            List<String> raw = redisTemplate.opsForList().range(listKey, 0, -1);
            if (raw == null || raw.isEmpty()) {
                return Collections.emptyList();
            }
            return raw.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.warn("Redis unavailable in getCompanyResponseIds, return empty for companyId={}", companyId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("Unexpected error in getCompanyResponseIds for companyId={}, return empty", companyId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Прогреть кэш: занести в Redis ID всех откликов этой компании
     */
    public void warmCompanyCache(Long companyId, List<ResponseApplication> responses) {
        for (ResponseApplication app : responses) {
            if (companyId.equals(app.getCompanyId())) {
                addResponseForCompany(app);
            }
        }
    }

    /**
     * Очистить кэш компании (если понадобится в будущем)
     */
    public void clearCompanyCache(Long companyId) {
        String listKey = companyResponsesKey(companyId);
        try {
            redisTemplate.delete(listKey);
        } catch (Exception e) {
            log.warn("Error clearing cache for companyId={}", companyId, e);
        }
    }
}
