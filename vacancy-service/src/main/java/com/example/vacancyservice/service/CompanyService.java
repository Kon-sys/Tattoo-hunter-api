package com.example.vacancyservice.service;

import com.example.vacancyservice.model.Company;
import com.example.vacancyservice.repo.CompanyRepository;
import com.example.vacancyservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public Optional<Company> findByUserLogin(String login) {
        Long userId = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login))
                .getId();
        return companyRepository.findByUserId(userId);
    }
}
