package com.example.chatservice.dto;

import com.example.chatservice.model.Chat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto {
    private Long id;
    private String employeeLogin;
    private Long companyId;
    private Long vacancyId;
    private LocalDateTime createdAt;
    private String companyName;
    private String vacancyName;
    private String employeeName;

    public static ChatDto from(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setEmployeeLogin(chat.getEmployeeLogin());
        dto.setCompanyId(chat.getCompanyId());
        dto.setVacancyId(chat.getVacancyId());
        dto.setCreatedAt(chat.getCreatedAt());
        return dto;
    }
}
