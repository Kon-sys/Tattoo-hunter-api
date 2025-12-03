package com.example.profileservice.service;

import com.example.profileservice.model.Employee;
import com.example.profileservice.model.User;
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
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            return Optional.empty();
        }
        System.out.println(user.getLogin());
        return employeeRepository.findByUser(user);
    }
}
