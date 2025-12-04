package com.example.chatservice.service;

import com.example.chatservice.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageCacheService {

    private final StringRedisTemplate redisTemplate;

    private String key(Long chatId) {
        return "chat:" + chatId + ":messageIds";
    }

    public void pushMessage(Message message) {
        Long chatId = message.getChat().getId();
        String key = key(chatId);

        try {
            redisTemplate.opsForList()
                    .rightPush(key, message.getId().toString());
        } catch (RedisConnectionFailureException e) {
            // Redis недоступен — просто молча игнорируем, работаем через БД
        } catch (DataAccessException e) {
            // Redis недоступен — просто молча игнорируем, работаем через БД
        }
    }

    public List<Long> getMessageIds(Long chatId) {
        String key = key(chatId);
        try {
            List<String> raw = redisTemplate.opsForList().range(key, 0, -1);
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
        } catch (RedisConnectionFailureException e) {
            // Redis упал — вернём пусто, дальше возьмём из БД
            return Collections.emptyList();
        } catch (DataAccessException e) {
            // Redis упал — вернём пусто, дальше возьмём из БД
            return Collections.emptyList();
        }
    }

    public void warmCache(Long chatId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) return;
        String key = key(chatId);

        try {
            redisTemplate.delete(key);
            for (Message m : messages) {
                redisTemplate.opsForList()
                        .rightPush(key, m.getId().toString());
            }
        } catch (RedisConnectionFailureException e) {
            // если Redis недоступен — просто игнорируем
        } catch (DataAccessException e) {
            // если Redis недоступен — просто игнорируем
        }
    }
}

