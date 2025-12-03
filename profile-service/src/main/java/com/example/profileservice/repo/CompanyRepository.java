package com.example.profileservice.repo;

import com.example.profileservice.model.Company;
import com.example.profileservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long id);
    Optional<Company> findByUserId(Long userId);
    Optional<Company> findByUser(User user);
}
