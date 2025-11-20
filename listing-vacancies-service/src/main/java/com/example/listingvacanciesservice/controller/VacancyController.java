package com.example.listingvacanciesservice.controller;

import com.example.listingvacanciesservice.dto.CompanyDto;
import com.example.listingvacanciesservice.dto.VacancyDto;
import com.example.listingvacanciesservice.dto.VacancyFilter;
import com.example.listingvacanciesservice.mapper.CompanyMapper;
import com.example.listingvacanciesservice.mapper.VacancyMapper;
import com.example.listingvacanciesservice.model.*;
import com.example.listingvacanciesservice.service.CompanyService;
import com.example.listingvacanciesservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    private final VacancyMapper vacancyMapper;

    private final CompanyService companyService;

    private final CompanyMapper companyMapper;

    @GetMapping
    public ResponseEntity<List<VacancyDto>> getVacancies() {

        List<Vacancy> vacancies = vacancyService.getVacancies();

        return ResponseEntity.ok().body(vacancies.stream()
                .map(vacancyMapper::toDto)
                .toList());
    }

    @GetMapping("/company")
    public ResponseEntity<?> findAllCompany(@RequestHeader("X_User_Role") String role){

        if (!"ROLE_EMPLOYEE".equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        List<Company> companies = companyService.findAll();

        List<CompanyDto> companiesDto = companies.stream()
                .map(companyMapper::toDto)   // или .map(c -> companyMapper.toDto(c))
                .toList();

        return ResponseEntity.ok().body(companiesDto);
    }

    @GetMapping
    @RequestMapping("/filter")
    public ResponseEntity<?> getFilteredVacancies(
            @RequestHeader("X_User_Role") String role,
            @RequestParam(required = false) String income,               // "договорная" или "5000"
            @RequestParam(required = false) Busy busy,                   // пример: ?busy=FULL_TIME
            @RequestParam(required = false) WorkSchedule workSchedule,   // пример: ?workSchedule=FIVE_TWO
            @RequestParam(required = false) WorkType workType,           // пример: ?workType=REMOTE
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minWorkingHours,
            @RequestParam(required = false) Integer maxWorkingHours,
            @RequestParam(required = false) String companyName
    ) {

        if (!"ROLE_EMPLOYEE".equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        VacancyFilter filter = new VacancyFilter();
        filter.setIncome(income);
        filter.setCompanyName(companyName);
        filter.setBusy(busy);
        filter.setWorkSchedule(workSchedule);
        filter.setWorkType(workType);
        filter.setMinExperience(minExperience);
        filter.setMaxExperience(maxExperience);
        filter.setMinWorkingHours(minWorkingHours);
        filter.setMaxWorkingHours(maxWorkingHours);

        List<Vacancy> vacancies = vacancyService.getVacanciesFiltered(filter);

        return ResponseEntity.ok(vacancies.stream()
                .map(vacancyMapper::toDto)
                .toList());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByTitle(
            @RequestHeader("X_User_Role") String role,
            @RequestParam String title) {

        if (!"ROLE_EMPLOYEE".equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        List<VacancyDto> result = vacancyService.getSearched(title).stream()
                .map(vacancyMapper::toDto)
                .toList();

        return ResponseEntity.ok(result);
    }

}
