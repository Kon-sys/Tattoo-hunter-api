package com.example.profileservice.controller;

import com.example.profileservice.config.S3Properties;
import com.example.profileservice.dto.EmployeeDTO;
import com.example.profileservice.dto.WorkCategoriesRequest;
import com.example.profileservice.exception.EmptyFileException;
import com.example.profileservice.exception.RoleException;
import com.example.profileservice.exception.UnauthorizedException;
import com.example.profileservice.model.Employee;
import com.example.profileservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.BiConsumer;
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
@RequestMapping("/api/profile/employee")
public class EmployeeProfileController {

    private final EmployeeService employeeService;

    private final S3Client s3Client;

    private final S3Properties props;

    /*
    POST method for adding information to employee
    Info for: FIRST NAME, LAST NAME, FATHER NAME, BIRTH DATE,
    GENDER, CITY, EXPERIENCE
     */
    @PostMapping
    public ResponseEntity<?> user_profile(@RequestHeader("X-User-Login") String login,
                                          @RequestHeader("X-User-Role") String role,
                                          @ModelAttribute EmployeeDTO form) {

        try {
            Employee employee = getEmployeeFromToken(login, role);
            employee.setFirstName(form.getFirstName());
            employee.setLastName(form.getLastName());
            employee.setFatherName(form.getFatherName());
            employee.setBirthDate(form.getBirthDate());
            employee.setGender(form.getGender());
            employee.setCity(form.getCity());
            employee.setExperience(form.getExperience());
            employeeService.save(employee);

            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setFirstName(employee.getFirstName());
            employeeDto.setLastName(employee.getLastName());
            employeeDto.setFatherName(employee.getFatherName());
            employeeDto.setBirthDate(employee.getBirthDate());
            employeeDto.setGender(employee.getGender());
            employeeDto.setCity(employee.getCity());
            employeeDto.setExperience(employee.getExperience());

            return ResponseEntity.ok(employeeDto);
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
    }

    /*
    POST method for adding information to employee
    Info for: WORK CATEGORY
     */
    @PostMapping("/set-work-categories")
    public ResponseEntity<?> setWorkCategories(@RequestHeader("X-User-Login") String login,
                                               @RequestHeader("X-User-Role") String role,
                                               @RequestBody WorkCategoriesRequest request) {

        System.out.println("LOGIN = " + login);
        System.out.println("ROLE  = " + role);
        System.out.println("CATS  = " + request.getWorkCategories());

        try {
            Employee employee = getEmployeeFromToken(login, role);
            employee.setWorkCategories(request.getWorkCategories());
            employeeService.save(employee);

            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setWorkCategories(employee.getWorkCategories()
                    != null ? employee.getWorkCategories() : null);

            return ResponseEntity.ok(employeeDto);
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/contact-details")
    public ResponseEntity<?> user_profile_more_info(@RequestHeader("X-User-Login") String login,
                                                    @RequestHeader("X-User-Role") String role,
                                                    @ModelAttribute EmployeeDTO form) {

        try {
            Employee employee = getEmployeeFromToken(login, role);

            employee.setPhone(form.getPhone());
            employee.setEmail(form.getEmail());
            employee.setTelegram(form.getTelegram());
            employeeService.save(employee);

            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setPhone(employee.getPhone());
            employeeDto.setEmail(employee.getEmail());
            employeeDto.setTelegram(employee.getTelegram());

            return ResponseEntity.ok(employeeDto);
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/additional-info")
    public ResponseEntity<?> user_profile_additional_info(@RequestHeader("X-User-Login") String login,
                                                          @RequestHeader("X-User-Role") String role,
                                                          @RequestBody EmployeeDTO form) {

        try {
            Employee employee = getEmployeeFromToken(login, role);

            employee.setAddInfo(form.getAddInfo());
            employeeService.save(employee);

            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setAddInfo(form.getAddInfo());

            return ResponseEntity.ok(employeeDto);
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping(value = "/set-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setResume(@RequestHeader("X-User-Login") String login,
                                       @RequestHeader("X-User-Role") String role,
                                       @RequestParam("file") MultipartFile file) {
        return handleFileUpload(login, role, file, "resumes", Employee::setResume);
    }

    @PostMapping(value = "/set-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setMainPhoto( @RequestHeader("X-User-Login") String login,
                                           @RequestHeader("X-User-Role") String role,
                                           @RequestParam("file") MultipartFile file) {
        return handleFileUpload(login, role, file, "main-photos", Employee::setMainPhoto);
    }

    private Employee getEmployeeFromToken(String login, String role) throws RoleException, UnauthorizedException {

        if (!"ROLE_EMPLOYEE".equals(role)) {
            throw new RoleException("Только сотрудник может редактировать профиль сотрудника");
        }

        Optional<Employee> employeeOpt = employeeService.findByUserLogin(login);

        if (employeeOpt.isEmpty()) {
            throw new UnauthorizedException("Профиль сотрудника для данного " +
                    "пользователя не найден или токен недействителен");
        }

        return employeeOpt.get();
    }

    private ResponseEntity<?> handleFileUpload(
            String login,
            String role,
            MultipartFile file,
            String s3Folder,
            BiConsumer<Employee, String> updateEmployeeField) {

        try {
            Employee employee = getEmployeeFromToken(login, role);

            String url = uploadFileToS3(file, s3Folder);
            updateEmployeeField.accept(employee, url);
            employeeService.save(employee);

            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setMainPhoto(employee.getMainPhoto());

            return ResponseEntity.ok(employeeDto);
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_SERVER_S3",
                    "message", e.getMessage()));
        }
        catch (EmptyFileException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "EMPTY_FILE",
                    "message", e.getMessage()));
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "ERROR TO EXCEPT FILE",
                    "message", e.getMessage()));
        }
    }


    private String uploadFileToS3(MultipartFile file, String folder) throws EmptyFileException, IOException {

        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Файл не передан");
        }

        String uniqueID = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
        String key = "tattoo-hunter/" + folder + "/" + timestamp + "_" + uniqueID + "_" + file.getOriginalFilename();

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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentEmployee(
            @RequestHeader("X-User-Login") String login,
            @RequestHeader("X-User-Role") String role
    ) {
        try {
            Employee employee = getEmployeeFromToken(login, role);

            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(employee.getId());
            dto.setUserId(
                    employee.getUser() != null ? employee.getUser().getId() : null
            );
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
        }
        catch (RoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "INVALID_ROLE",
                            "message", e.getMessage()
                    ));
        }
        catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "EMPLOYEE_NOT_FOUND",
                            "message", e.getMessage()
                    ));
        }
    }


}

