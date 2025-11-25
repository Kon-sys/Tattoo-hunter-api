package com.example.applicationsservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChatClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${chat.service.url:http://localhost:8084/api/chats}")
    private String chatServiceUrl;

    public void openChat(String employeeLogin, Long companyId, Long vacancyId) {
        ChatOpenRequest req = new ChatOpenRequest(employeeLogin, companyId, vacancyId);
        restTemplate.postForEntity(chatServiceUrl + "/open", req, Void.class);
    }

    @Data
    @AllArgsConstructor
    public static class ChatOpenRequest {
        private String employeeLogin;
        private Long companyId;
        private Long vacancyId;
    }
}
