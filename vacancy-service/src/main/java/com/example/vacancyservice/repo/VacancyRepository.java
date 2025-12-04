package com.example.vacancyservice.repo;

import com.example.vacancyservice.model.Company;
import com.example.vacancyservice.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Optional<Vacancy> findById(Long id);
    List<Vacancy> findAll();

    List<Vacancy> findByCompany(Company company);
}
