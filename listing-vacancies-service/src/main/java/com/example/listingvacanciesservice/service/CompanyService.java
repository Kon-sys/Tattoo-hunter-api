package com.example.listingvacanciesservice.service;

import com.example.listingvacanciesservice.model.Company;
import com.example.listingvacanciesservice.repo.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll(){
        return companyRepository.findAll();
    }
}
