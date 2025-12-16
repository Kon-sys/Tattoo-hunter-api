package com.example.adminservice.repo;

import com.example.adminservice.dto.ChatDurationItemDto;
import com.example.adminservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageAnalyticsRepository extends JpaRepository<Message, Long> {

    @Query("""
           select new com.example.adminservice.dto.ChatDurationItemDto(
               c.id,
               c.employeeLogin,
               c.companyId,
               c.vacancyId,
               min(m.createdAt),
               max(m.createdAt),
               count(m)
           )
           from Message m
           join m.chat c
           group by c.id, c.employeeLogin, c.companyId, c.vacancyId
           """)
    List<ChatDurationItemDto> findChatDurations();
}

