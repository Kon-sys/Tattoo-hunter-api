package com.example.vacancyservice.controller;

import com.example.vacancyservice.config.S3Properties;
import com.example.vacancyservice.dto.VacancyDto;
import com.example.vacancyservice.exception.NotFoundVacancyException;
import com.example.vacancyservice.exception.RoleException;
import com.example.vacancyservice.mapper.VacancyMapper;
import com.example.vacancyservice.model.Company;
import com.example.vacancyservice.model.Vacancy;
import com.example.vacancyservice.service.CompanyService;
import com.example.vacancyservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vacancy")
public class VacancyController {

    private final VacancyService vacancyService;

    private final CompanyService companyService;

    private final VacancyMapper vacancyMapper;

    private final S3Client s3Client;

    private final S3Properties props;

    @PostMapping
    public ResponseEntity<?> createVacancy(@RequestHeader("X_User_Login") String login,
                                           @RequestHeader("X_User_Role") String role,
                                           @ModelAttribute VacancyDto form) {

        try {
            if (!"ROLE_COMPANY".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                        .body(Map.of(
                                "error", "INVALID_ROLE",
                                "message", "Только компания может редактировать вакансии"
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

            Vacancy vacancy = new Vacancy();
            vacancy.setCompany(companyOpt.get());
            vacancy.setTitle(form.getTitle());
            vacancy.setIncome_level(form.getIncomeLevel());
            vacancy.setBusy(form.getBusy());
            vacancy.setExperience(form.getExperience());
            vacancy.setWorkSchedule(form.getWorkSchedule());
            vacancy.setWorking_hours(form.getWorkingHours());
            vacancy.setWorkType(form.getWorkType());
            vacancyService.save(vacancy);

            return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "USER_NOT_FOUND",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/additional-info/{vacancyId}")
    public ResponseEntity<?> additional_info(@RequestHeader("X_User_Role") String role,
                                             @PathVariable Long vacancyId,
                                             @RequestBody String additional_info) {

        try {

            Vacancy vacancy = getVacancy(role, vacancyId);
            vacancy.setAddInfo(additional_info);
            vacancyService.save(vacancy);

            return ResponseEntity.ok(vacancyMapper.toDto(vacancy));

        } catch(RoleException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        } catch(NotFoundVacancyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "VACANCY_NOT_FOUND",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/listing-photo/{vacancyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> listing_photo_url(@RequestHeader("X_User_Role") String role,
                                               @PathVariable Long vacancyId,
                                               @RequestParam("file") MultipartFile file){
        try{

            Vacancy vacancy = getVacancy(role, vacancyId);

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл не передан");
            }

            String url = uploadFileToS3(file);
            vacancy.setList_url(url);
            vacancyService.save(vacancy);
            return ResponseEntity.ok(vacancyMapper.toDto(vacancy));

        } catch(RoleException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        } catch(NotFoundVacancyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "VACANCY_NOT_FOUND",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "NO_FILE_TO_UPLOAD",
                    "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "BAD_SERVER_S3_ERROR",
                    "message", e.getMessage()));
        }
    }

    private String uploadFileToS3(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не передан");
        }

        String uniqueID = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
        String key = "tattoo-hunter/" + "vacancy-listing" + "/" + timestamp + "_" + uniqueID + "_" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(props.getBucket())
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );

        return props.getEndpoint() + "/" + props.getBucket() + "/" + key;
    }

    private Vacancy getVacancy(String role, Long vacancyId) throws RoleException, NotFoundVacancyException {
        if (!"ROLE_EMPLOYEE".equals(role)) {
            throw new RoleException("Только компания может редактировать вакансию");
        }

        Optional<Vacancy> vacancyOpt = vacancyService.findById(vacancyId);

        if (vacancyOpt.isEmpty()) {
            throw new NotFoundVacancyException("Вакансия не найдена");
        }
        return vacancyOpt.get();
    }
}
