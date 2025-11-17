package com.example.profileservice.service;

import com.example.profileservice.model.Company;
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
        return companyRepository.findByUserId(userRepository.findByLogin(userLogin).getId());
    }
}
