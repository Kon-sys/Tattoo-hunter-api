package com.example.adminservice.service;

import com.example.adminservice.dto.CompanyOptionDto;
import com.example.adminservice.repo.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyOptionDto> getOptions() {
        return companyRepository.findAllByOrderByNameAsc()
                .stream()
                .map(c -> new CompanyOptionDto(c.getId(), c.getName()))
                .toList();
    }
}
