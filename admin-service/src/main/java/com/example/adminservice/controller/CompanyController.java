package com.example.adminservice.controller;

import com.example.adminservice.dto.CompanyOptionDto;
import com.example.adminservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/options")
    public List<CompanyOptionDto> options() {
        return companyService.getOptions();
    }
}
