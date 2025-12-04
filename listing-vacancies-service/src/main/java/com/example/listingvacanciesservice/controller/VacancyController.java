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
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getVacancyById(@PathVariable Long id) {
        Optional<Vacancy> vacancyOpt = vacancyService.findById(id);

        if (vacancyOpt.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "VACANCY_NOT_FOUND",
                            "message", "Вакансия не найдена"
                    )
            );
        }

        VacancyDto dto = vacancyMapper.toDto(vacancyOpt.get());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/company")
    public ResponseEntity<?> findAllCompany(@RequestHeader("X_User_Role") String role) {

        if (!"ROLE_EMPLOYEE".equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        List<Company> companies = companyService.findAll();

        List<CompanyDto> companiesDto = companies.stream()
                .map(companyMapper::toDto)
                .toList();

        return ResponseEntity.ok(companiesDto);
    }


    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredVacancies(
            @RequestHeader("X_User_Role") String role,
            @RequestParam(required = false) String income,
            @RequestParam(required = false) Busy busy,
            @RequestParam(required = false) WorkSchedule workSchedule,
            @RequestParam(required = false) WorkType workType,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minWorkingHours,
            @RequestParam(required = false) Integer maxWorkingHours,
            // принимаем как строки
            @RequestParam(required = false, name = "companyIds") List<String> companyIdsRaw,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir
    ) {

        if (!"ROLE_EMPLOYEE".equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        VacancyFilter filter = new VacancyFilter();
        filter.setIncome(income);
        filter.setBusy(busy);
        filter.setWorkSchedule(workSchedule);
        filter.setWorkType(workType);
        filter.setMinExperience(minExperience);
        filter.setMaxExperience(maxExperience);
        filter.setMinWorkingHours(minWorkingHours);
        filter.setMaxWorkingHours(maxWorkingHours);

        // 🔥 аккуратно парсим companyIdsRaw → List<Long>
        if (companyIdsRaw != null && !companyIdsRaw.isEmpty()) {
            List<Long> companyIds = companyIdsRaw.stream()
                    .flatMap(s -> java.util.Arrays.stream(s.split(",")))
                    .map(String::trim)
                    .filter(str -> !str.isEmpty())
                    .map(str -> {
                        try {
                            return Long.parseLong(str);
                        } catch (NumberFormatException e) {
                            System.out.println("Не получилось распарсить companyId: " + str);
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .toList();

            System.out.println("DEBUG /filter companyIdsParsed = " + companyIds);
            filter.setCompanyIds(companyIds);
        } else {
            System.out.println("DEBUG /filter companyIdsRaw = null/empty");
        }

        List<Vacancy> vacancies = vacancyService.getVacanciesFiltered(filter, sortBy, sortDir);

        return ResponseEntity.ok(
                vacancies.stream()
                        .map(vacancyMapper::toDto)
                        .toList()
        );
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
