package com.example.applicationsservice.repo;

import com.example.applicationsservice.model.ResponseApplication;
import com.example.applicationsservice.model.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<ResponseApplication, Long> {

    List<ResponseApplication> findByEmployeeLogin(String employeeLogin);

    List<ResponseApplication> findByCompanyIdAndStatus(Long companyId, ResponseStatus status);
}