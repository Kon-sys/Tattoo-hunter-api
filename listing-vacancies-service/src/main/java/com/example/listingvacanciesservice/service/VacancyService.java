package com.example.listingvacanciesservice.service;

import com.example.listingvacanciesservice.dto.VacancyDto;
import com.example.listingvacanciesservice.dto.VacancyFilter;
import com.example.listingvacanciesservice.model.Vacancy;
import com.example.listingvacanciesservice.repo.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    private final VacancyFilterService vacancyFilterService;

    public List<Vacancy> getVacancies() {
        return vacancyRepository.findAll();
    }
    public List<Vacancy> getVacanciesFiltered(VacancyFilter vacancyFilter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        return vacancyFilterService.filterVacancies(vacancies, vacancyFilter);
    }

    public List<Vacancy> getSearched(String title){
        return vacancyRepository.findByTitleContainingIgnoreCase(title);
    }
}
