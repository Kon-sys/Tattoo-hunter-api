package com.example.chatservice.dto;

import com.example.chatservice.model.Message;
import com.example.chatservice.model.SenderRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long chatId;
    private String senderLogin;
    private SenderRole senderRole;
    private String text;
    private LocalDateTime createdAt;

    public static MessageDto from(Message msg) {
        MessageDto dto = new MessageDto();
        dto.setId(msg.getId());
        dto.setChatId(msg.getChat().getId());
        dto.setSenderLogin(msg.getSenderLogin());
        dto.setSenderRole(msg.getSenderRole());
        dto.setText(msg.getText());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}
