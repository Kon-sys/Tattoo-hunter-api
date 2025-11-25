package com.example.applicationsservice.service;

import com.example.applicationsservice.model.ResponseApplication;
import com.example.applicationsservice.model.ResponseStatus;
import com.example.applicationsservice.repo.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final NotificationService notificationService;
    private final ChatClient chatClient;

    @Transactional
    public ResponseApplication createResponse(Long vacancyId,
                                              Long companyId,
                                              String employeeLogin) {
        ResponseApplication app = new ResponseApplication();
        app.setVacancyId(vacancyId);
        app.setCompanyId(companyId);
        app.setEmployeeLogin(employeeLogin);
        app.setStatus(ResponseStatus.PENDING);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        ResponseApplication saved = responseRepository.save(app);

        notificationService.addNewResponseForCompany(companyId, saved.getId());

        return saved;
    }

    public List<ResponseApplication> getEmployeeResponses(String employeeLogin) {
        return responseRepository.findByEmployeeLogin(employeeLogin);
    }

    public List<ResponseApplication> getCompanyResponses(Long companyId, ResponseStatus status) {
        return responseRepository.findByCompanyIdAndStatus(companyId, status);
    }

    @Transactional
    public ResponseApplication approveResponse(Long responseId) {
        ResponseApplication app = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("Response not found"));

        app.setStatus(ResponseStatus.APPROVED);
        app.setUpdatedAt(LocalDateTime.now());
        ResponseApplication saved = responseRepository.save(app);

        // открыть чат (или получить существующий)
        chatClient.openChat(saved.getEmployeeLogin(), saved.getCompanyId(), saved.getVacancyId());

        return saved;
    }


    @Transactional
    public ResponseApplication rejectResponse(Long responseId) {
        ResponseApplication app = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("Response not found"));

        app.setStatus(ResponseStatus.REJECTED);
        app.setUpdatedAt(LocalDateTime.now());

        return responseRepository.save(app);
    }
}
