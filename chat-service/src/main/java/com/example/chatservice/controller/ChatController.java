package com.example.chatservice.controller;

import com.example.chatservice.client.UserInfoDto;
import com.example.chatservice.dto.ChatDto;
import com.example.chatservice.dto.MessageDto;
import com.example.chatservice.model.Chat;
import com.example.chatservice.model.Message;
import com.example.chatservice.model.SenderRole;
import com.example.chatservice.service.AuthClient;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.service.UserDetailsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AuthClient authClient;
    private final UserDetailsService userDetailsService;

    /**
     * Этот эндпоинт дергает response-service при approve отклика.
     * Тут нет X-User-Login, это внутренний вызов сервис->сервис.
     */
    @PostMapping("/open")
    public ResponseEntity<ChatDto> openChat(@RequestBody OpenChatRequest request) {
        Chat chat = chatService.openOrGetChat(
                request.getEmployeeLogin(),
                request.getCompanyId(),
                request.getVacancyId()
        );
        return ResponseEntity.ok(ChatDto.from(chat));
    }

    /**
     * Список чатов работника (по логину).
     */
    @GetMapping("/employee")
    public ResponseEntity<List<ChatDto>> getEmployeeChats(
            @RequestHeader("X-User-Login") String login
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_EMPLOYEE".equals(user.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatDto> result = chatService.getChatsForEmployee(login).stream()
                .map(ChatDto::from)
                .toList();


        for (ChatDto chatDto : result) {
            chatDto.setVacancyName(userDetailsService.getVacancyName(chatDto.getCompanyId(), chatDto.getVacancyId()));
            chatDto.setCompanyName(userDetailsService.getCompanyName(chatDto.getCompanyId()));
        }


        return ResponseEntity.ok(result);
    }

    /**
     * Список чатов компании.
     * companyId пока передаётся параметром (у тебя нет привязки Company <-> User).
     */
    @GetMapping("/company")
    public ResponseEntity<List<ChatDto>> getCompanyChats(
            @RequestHeader("X-User-Login") String login,
            @RequestParam Long companyId
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_COMPANY".equals(user.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatDto> result = chatService.getChatsForCompany(companyId).stream()
                .map(ChatDto::from)
                .toList();

        for (ChatDto chatDto : result) {
            chatDto.setVacancyName(userDetailsService.getVacancyName(chatDto.getCompanyId(), chatDto.getVacancyId()));
            chatDto.setEmployeeName(userDetailsService.getEmployeeName(chatDto.getEmployeeLogin()));
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Сообщения в чате.
     * Сейчас без жёсткой проверки, что пользователь имеет доступ к этому чату.
     * (можно будет допилить позже).
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @RequestHeader("X-User-Login") String login,
            @PathVariable Long chatId
    ) {
        // можно тут проверять login против chat.employeeLogin или companyId
        List<MessageDto> result = chatService.getMessages(chatId).stream()
                .map(MessageDto::from)
                .toList();

        for (MessageDto messageDto : result) {
            messageDto.setSenderName(userDetailsService.getSenderName(messageDto.getSenderLogin(), messageDto.getSenderRole()));
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Отправка сообщения.
     * Роль берём из auth-service по логину.
     */
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("X-User-Login") String login,
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null) {
            return ResponseEntity.badRequest().body("Unknown user: " + login);
        }

        SenderRole senderRole;
        if ("ROLE_EMPLOYEE".equals(user.getRole())) {
            senderRole = SenderRole.EMPLOYEE;
        } else if ("ROLE_COMPANY".equals(user.getRole())) {
            senderRole = SenderRole.COMPANY;
        } else {
            return ResponseEntity.badRequest().body("Unknown role: " + user.getRole());
        }

        try {
            Message msg = chatService.sendMessage(chatId, login, senderRole, request.getText());
            MessageDto messageDto = MessageDto.from(msg);
            messageDto.setSenderName(userDetailsService.getSenderName(login, senderRole));
            return ResponseEntity.ok(messageDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error sending message: " + ex.getMessage());
        }
    }



    // DTO для запросов

    @Data
    public static class OpenChatRequest {
        private String employeeLogin;
        private Long companyId;
        private Long vacancyId;
    }

    @Data
    public static class SendMessageRequest {
        private String text;
    }
}
