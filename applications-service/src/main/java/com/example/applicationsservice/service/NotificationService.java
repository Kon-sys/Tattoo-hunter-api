package com.example.applicationsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final StringRedisTemplate redisTemplate;

    public void addNewResponseForCompany(Long companyId, Long responseId) {
        String countKey = "company:" + companyId + ":newResponsesCount";
        String listKey = "company:" + companyId + ":newResponses";

        redisTemplate.opsForList().leftPush(listKey, responseId.toString());
        redisTemplate.opsForValue().increment(countKey);
    }

    public Long getNewResponsesCount(Long companyId) {
        String countKey = "company:" + companyId + ":newResponsesCount";
        String value = redisTemplate.opsForValue().get(countKey);
        return value == null ? 0L : Long.parseLong(value);
    }

    public void resetNewResponses(Long companyId) {
        String countKey = "company:" + companyId + ":newResponsesCount";
        String listKey = "company:" + companyId + ":newResponses";

        redisTemplate.delete(countKey);
        redisTemplate.delete(listKey);
    }
}
