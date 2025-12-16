package com.example.adminservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.Duration;

@Data
public class ChatDurationItemDto {

    private Long chatId;
    private String employeeLogin;
    private Long companyId;
    private Long vacancyId;
    private LocalDateTime firstMessage;
    private LocalDateTime lastMessage;
    private long durationMinutes;
    private long messagesCount;

    public ChatDurationItemDto(Long chatId,
                               String employeeLogin,
                               Long companyId,
                               Long vacancyId,
                               LocalDateTime firstMessage,
                               LocalDateTime lastMessage,
                               long messagesCount) {
        this.chatId = chatId;
        this.employeeLogin = employeeLogin;
        this.companyId = companyId;
        this.vacancyId = vacancyId;
        this.firstMessage = firstMessage;
        this.lastMessage = lastMessage;
        this.messagesCount = messagesCount;

        // защита от null/одного сообщения
        if (firstMessage != null && lastMessage != null) {
            this.durationMinutes = Duration.between(firstMessage, lastMessage).toMinutes();
        } else {
            this.durationMinutes = 0;
        }
    }
}

