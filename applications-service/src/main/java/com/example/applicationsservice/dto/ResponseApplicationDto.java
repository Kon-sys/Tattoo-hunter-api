package com.example.applicationsservice.dto;

import com.example.applicationsservice.model.ResponseApplication;
import com.example.applicationsservice.model.ResponseStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseApplicationDto {
    private Long id;
    private Long vacancyId;
    private Long companyId;
    private String employeeLogin;
    private ResponseStatus status;
    private LocalDateTime createdAt;

    public static ResponseApplicationDto from(ResponseApplication app) {
        ResponseApplicationDto dto = new ResponseApplicationDto();
        dto.setId(app.getId());
        dto.setVacancyId(app.getVacancyId());
        dto.setCompanyId(app.getCompanyId());
        dto.setEmployeeLogin(app.getEmployeeLogin());
        dto.setStatus(app.getStatus());
        dto.setCreatedAt(app.getCreatedAt());
        return dto;
    }
}
