package com.example.chatservice.repo;

import com.example.chatservice.model.Chat;
import com.example.chatservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatOrderByCreatedAtAsc(Chat chat);
}

