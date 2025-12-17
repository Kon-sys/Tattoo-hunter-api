package com.example.adminservice.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ChatDurationItemDto {
    private Long chatId;
    private Long companyId;
    private Long vacancyId;
    private String employeeLogin;
    private LocalDateTime firstMessage;
    private LocalDateTime lastMessage;
    private Long messagesCount;
    private long durationMinutes;

    private String companyName;
    private String employeeFirstName;
    private String employeeLastName;

    public ChatDurationItemDto(
            Long chatId,
            Long companyId,
            Long vacancyId,
            String employeeLogin,
            LocalDateTime firstMessage,
            LocalDateTime lastMessage,
            Long messagesCount
    ) {
        this.chatId = chatId;
        this.companyId = companyId;
        this.vacancyId = vacancyId;
        this.employeeLogin = employeeLogin;
        this.firstMessage = firstMessage;
        this.lastMessage = lastMessage;
        this.messagesCount = messagesCount;
        this.durationMinutes = (firstMessage != null && lastMessage != null)
                ? Duration.between(firstMessage, lastMessage).toMinutes()
                : 0;
    }
}
