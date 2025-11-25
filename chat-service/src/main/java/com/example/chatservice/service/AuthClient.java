package com.example.chatservice.service;

import com.example.chatservice.client.UserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public UserInfoDto getUserByLogin(String login) {
        String url = authServiceUrl + "/by-login?login=" + login;
        return restTemplate.getForObject(url, UserInfoDto.class);
    }
}
