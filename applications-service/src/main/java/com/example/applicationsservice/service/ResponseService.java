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

        // ✅ кладём id отклика в Redis-список компании
        notificationService.addResponseForCompany(saved);

        return saved;
    }

    public List<ResponseApplication> getEmployeeResponses(String employeeLogin) {
        return responseRepository.findByEmployeeLogin(employeeLogin);
    }

    @Transactional(readOnly = true)
    public List<ResponseApplication> getCompanyResponses(Long companyId, ResponseStatus status) {
        // 1. Пытаемся взять список id из Redis
        var idsFromRedis = notificationService.getCompanyResponseIds(companyId);

        if (idsFromRedis != null && !idsFromRedis.isEmpty()) {
            // ⚠️ важно: не делаем findById в цикле, а одной операцией
            List<ResponseApplication> cached = responseRepository.findAllById(idsFromRedis);

            return cached.stream()
                    .filter(a -> companyId.equals(a.getCompanyId()))
                    .filter(a -> status == null || a.getStatus() == status)
                    .toList();
        }

        // 2. Если кэш пуст — читаем из БД
        List<ResponseApplication> fromDb =
                responseRepository.findByCompanyIdAndStatus(companyId, status);

        // 3. Прогреваем кэш на будущее
        notificationService.warmCompanyCache(companyId, fromDb);

        return fromDb;
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
