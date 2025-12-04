package com.example.applicationsservice.controller;

import com.example.applicationsservice.dto.ResponseApplicationDto;
import com.example.applicationsservice.dto.UserInfoDto;
import com.example.applicationsservice.model.ResponseApplication;
import com.example.applicationsservice.model.ResponseStatus;
import com.example.applicationsservice.service.AuthClient;
import com.example.applicationsservice.service.ResponseService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;
    private final AuthClient authClient;

    // работник откликается на вакансию
    @PostMapping
    public ResponseEntity<?> respondToVacancy(
            @RequestHeader("X-User-Login") String login,
            @RequestBody RespondRequest request
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_EMPLOYEE".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Only employee can respond");
        }

        ResponseApplication app = responseService.createResponse(
                request.getVacancyId(),
                request.getCompanyId(),
                login
        );

        return ResponseEntity.ok(ResponseApplicationDto.from(app));
    }

    // мои отклики (для работника)
    @GetMapping("/employee")
    public ResponseEntity<List<ResponseApplicationDto>> getEmployeeResponses(
            @RequestHeader("X-User-Login") String login
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_EMPLOYEE".equals(user.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        List<ResponseApplicationDto> result = responseService
                .getEmployeeResponses(login)
                .stream()
                .map(ResponseApplicationDto::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    // отклики компании (по companyId из запроса)
    @GetMapping("/company")
    public ResponseEntity<?> getCompanyResponses(
            @RequestHeader("X-User-Login") String login,
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "PENDING") ResponseStatus status
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_COMPANY".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Only company can view responses");
        }

        List<ResponseApplicationDto> result = responseService
                .getCompanyResponses(companyId, status)
                .stream()
                .map(ResponseApplicationDto::from)
                .toList();

        return ResponseEntity.ok(result);
    }


    // компания одобряет отклик (чат создадим уже в chat-service)
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @RequestHeader("X-User-Login") String login,
            @PathVariable Long id
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_COMPANY".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Only company can approve responses");
        }

        ResponseApplication app = responseService.approveResponse(id);
        return ResponseEntity.ok(ResponseApplicationDto.from(app));
    }

    // отклонение
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @RequestHeader("X-User-Login") String login,
            @PathVariable Long id
    ) {
        UserInfoDto user = authClient.getUserByLogin(login);
        if (user == null || !"ROLE_COMPANY".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Only company can reject responses");
        }

        ResponseApplication app = responseService.rejectResponse(id);
        return ResponseEntity.ok(ResponseApplicationDto.from(app));
    }

    @Data
    public static class RespondRequest {
        private Long vacancyId;
        private Long companyId;
    }
}
