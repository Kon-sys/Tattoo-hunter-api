package com.example.vacancyservice.service;

import com.example.vacancyservice.model.Vacancy;
import com.example.vacancyservice.repo.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    public void save(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }

    public Optional<Vacancy> findById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId);
    }
}
