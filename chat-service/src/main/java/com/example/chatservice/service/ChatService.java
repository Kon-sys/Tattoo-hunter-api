package com.example.chatservice.service;

import com.example.chatservice.model.Chat;
import com.example.chatservice.model.Message;
import com.example.chatservice.model.SenderRole;
import com.example.chatservice.repo.ChatRepository;
import com.example.chatservice.repo.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public Chat openOrGetChat(String employeeLogin, Long companyId, Long vacancyId) {
        return chatRepository
                .findByEmployeeLoginAndCompanyIdAndVacancyId(employeeLogin, companyId, vacancyId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setEmployeeLogin(employeeLogin);
                    chat.setCompanyId(companyId);
                    chat.setVacancyId(vacancyId);
                    chat.setCreatedAt(LocalDateTime.now());
                    return chatRepository.save(chat);
                });
    }

    public List<Chat> getChatsForEmployee(String employeeLogin) {
        return chatRepository.findByEmployeeLogin(employeeLogin);
    }

    public List<Chat> getChatsForCompany(Long companyId) {
        return chatRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Message sendMessage(Long chatId, String senderLogin, SenderRole senderRole, String text) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message msg = new Message();
        msg.setChat(chat);
        msg.setSenderLogin(senderLogin);
        msg.setSenderRole(senderRole);
        msg.setText(text);
        msg.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    public List<Message> getMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        return messageRepository.findByChatOrderByCreatedAtAsc(chat);
    }
}
