package com.example.profileservice.controller;


import com.example.profileservice.dto.CompanyDTO;
import com.example.profileservice.dto.EmployeeDTO;
import com.example.profileservice.exception.UnauthorizedException;
import com.example.profileservice.model.Company;
import com.example.profileservice.model.Employee;
import com.example.profileservice.model.User;
import com.example.profileservice.repo.EmployeeRepository;
import com.example.profileservice.repo.UserRepository;
import com.example.profileservice.service.CompanyService;
import com.example.profileservice.service.EmployeeService;
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
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<?> register_company(@RequestHeader("X-User-Login") String login,
                                              @RequestHeader("X-User-Role") String role,
                                              @ModelAttribute CompanyDTO form) {
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



    @GetMapping("/view/{employeeLogin}")
    public ResponseEntity<?> getEmployeeProfileForCompany(
            @RequestHeader("X-User-Login") String requesterLogin,
            @RequestHeader("X-User-Role") String requesterRole,
            @PathVariable String employeeLogin
    ) {
        try {
            // Смотреть может компания (и при желании сам работник)
            if (!"ROLE_COMPANY".equals(requesterRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "INVALID_ROLE", "message", "Access denied"));
            }
            User user = userRepository.findByLogin(employeeLogin);
            Employee employee = employeeRepository.findByUser(user)
                    .orElseThrow(() -> new UnauthorizedException("Employee not found: " + employeeLogin));

            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(employee.getId());
            dto.setUserId(employee.getUser() != null ? employee.getUser().getId() : null);
            dto.setFirstName(employee.getFirstName());
            dto.setLastName(employee.getLastName());
            dto.setFatherName(employee.getFatherName());
            dto.setBirthDate(employee.getBirthDate());
            dto.setGender(employee.getGender());
            dto.setCity(employee.getCity());
            dto.setExperience(employee.getExperience());
            dto.setPhone(employee.getPhone());
            dto.setEmail(employee.getEmail());
            dto.setTelegram(employee.getTelegram());
            dto.setWorkCategories(employee.getWorkCategories());
            dto.setAddInfo(employee.getAddInfo());
            dto.setMainPhoto(employee.getMainPhoto());
            dto.setResume(employee.getResume());

            return ResponseEntity.ok(dto);

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "EMPLOYEE_NOT_FOUND", "message", e.getMessage()));
        }
    }

}

