package com.example.applicationsservice.service;

import com.example.applicationsservice.model.ResponseApplication;
import com.example.applicationsservice.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseCacheService {

    private final StringRedisTemplate redisTemplate;

    private String key(Long companyId, ResponseStatus status) {
        return "company:" + companyId + ":responses:" + status.name();
    }

    public void cacheResponse(ResponseApplication app) {
        Long companyId = app.getCompanyId();
        ResponseStatus status = app.getStatus();
        String key = key(companyId, status);

        try {
            redisTemplate.opsForList()
                    .rightPush(key, app.getId().toString());
        } catch (DataAccessException e) {
            // Redis недоступен — просто игнорируем, БД всё равно работает
        }
    }

    public List<Long> getResponseIds(Long companyId, ResponseStatus status) {
        String key = key(companyId, status);
        try {
            List<String> raw = redisTemplate.opsForList()
                    .range(key, 0, -1);

            if (raw == null || raw.isEmpty()) {
                return Collections.emptyList();
            }

            List<Long> ids = new ArrayList<>();
            for (String s : raw) {
                try {
                    ids.add(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                }
            }
            return ids;

        } catch (DataAccessException e) {
            // нет Redis — вернём пустой список, дальше возьмём из БД
            return Collections.emptyList();
        }
    }

    public void warmCache(Long companyId,
                          ResponseStatus status,
                          List<ResponseApplication> responses) {
        if (responses == null || responses.isEmpty()) return;

        String key = key(companyId, status);

        try {
            redisTemplate.delete(key);
            for (ResponseApplication app : responses) {
                redisTemplate.opsForList()
                        .rightPush(key, app.getId().toString());
            }
        } catch (DataAccessException e) {
            // Redis упал — просто игнорируем
        }
    }
}
