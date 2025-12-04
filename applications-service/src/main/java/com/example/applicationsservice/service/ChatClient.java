package com.example.applicationsservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChatClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // тут только host:port
    @Value("${chat.service.url:http://localhost:8086}")
    private String chatServiceUrl;

    public void openChat(String employeeLogin, Long companyId, Long vacancyId) {
        String url = chatServiceUrl + "/api/chats/open";

        ChatOpenRequest req = new ChatOpenRequest(employeeLogin, companyId, vacancyId);
        restTemplate.postForEntity(url, req, Void.class);
    }

    @Data
    @AllArgsConstructor
    public static class ChatOpenRequest {
        private String employeeLogin;
        private Long companyId;
        private Long vacancyId;
    }
}
