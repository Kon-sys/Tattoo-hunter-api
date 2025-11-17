package com.example.profileservice.controller;


import com.example.profileservice.dto.CompanyDTO;
import com.example.profileservice.model.Company;
import com.example.profileservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/profile/company")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> register_company(@RequestHeader("X-User-Login") String login,
                                              @RequestHeader("X-User-Role") String role,
                                              @ModelAttribute Company form) {
        if (!"ROLE_COMPANY".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", "Только компания может редактировать профиль компании"
                    ));
        }

        Optional<Company> companyOpt = companyService.findByUserLogin(login);

        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "COMPANY_NOT_FOUND",
                            "message", "Профиль компании для данного пользователя не найден или токен недействителен"
                    ));
        }

        Company company = companyOpt.get();

        company.setName(form.getName());
        company.setCity(form.getCity());
        company.setAddress(form.getAddress());
        companyService.save(company);
        CompanyDTO companyDto = new CompanyDTO();
        companyDto.setId(company.getId());
        companyDto.setName(company.getName());
        companyDto.setCity(company.getCity());
        companyDto.setAddress(company.getAddress());

        return ResponseEntity.ok(companyDto);
    }

}

