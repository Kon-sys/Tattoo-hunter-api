package com.example.chatservice.repo;

import com.example.chatservice.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByEmployeeLoginAndCompanyIdAndVacancyId(String employeeLogin,
                                                               Long companyId,
                                                               Long vacancyId);

    List<Chat> findByEmployeeLogin(String employeeLogin);

    List<Chat> findByCompanyId(Long companyId);
}

