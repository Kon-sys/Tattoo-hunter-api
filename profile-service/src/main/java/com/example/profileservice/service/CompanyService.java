package com.example.profileservice.service;

import com.example.profileservice.model.Company;
import com.example.profileservice.model.User;
import com.example.profileservice.repo.CompanyRepository;
import com.example.profileservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public void save(Company company) {
        companyRepository.save(company);
    }

    public Optional<Company> findByUserLogin(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            return Optional.empty();
        }

        // Хорошо бы, чтобы тут была Optional<User> findByLogin(...)
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            // профайл-сервис не знает про такого юзера
            return Optional.empty();
        }
        return companyRepository.findByUser(user);
    }
}
