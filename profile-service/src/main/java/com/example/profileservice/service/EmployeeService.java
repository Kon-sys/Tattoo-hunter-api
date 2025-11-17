package com.example.profileservice.service;

import com.example.profileservice.model.Employee;
import com.example.profileservice.repo.EmployeeRepository;
import com.example.profileservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserRepository userRepository;

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public Optional<Employee> findByUserLogin(String userLogin) {
        return employeeRepository.findByUserId(userRepository.findByLogin(userLogin).getId());
    }
}
