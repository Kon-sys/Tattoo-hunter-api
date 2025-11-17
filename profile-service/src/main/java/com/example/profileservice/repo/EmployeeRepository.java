package com.example.profileservice.repo;

import com.example.profileservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findById(Long Id);
    Optional<Employee> findByUserId(Long userId);
}
