package com.example.listingvacanciesservice.service;

import com.example.listingvacanciesservice.dto.VacancyFilter;
import com.example.listingvacanciesservice.model.Vacancy;
import com.example.listingvacanciesservice.repo.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    private final VacancyFilterService vacancyFilterService;

    public List<Vacancy> getVacancies() {
        return vacancyRepository.findAll();
    }

    public List<Vacancy> getVacanciesFiltered(VacancyFilter filter, String sortBy, String sortDir) {
        List<Vacancy> base = vacancyRepository.findAll();
        List<Vacancy> filtered = vacancyFilterService.filterVacancies(base, filter);

        Comparator<Vacancy> comparator = buildComparator(sortBy, sortDir);

        return filtered.stream()
                .sorted(comparator)
                .toList();
    }

    private Comparator<Vacancy> buildComparator(String sortBy, String sortDir) {
        Comparator<Vacancy> comparator;

        // сортируем только по income или experience
        switch (sortBy.toLowerCase()) {
            case "experience" ->
                    comparator = Comparator.comparingInt(Vacancy::getExperience);
            case "income" ->
                    comparator = Comparator.comparingInt(this::extractIncomeValue);
            default ->
                // по умолчанию пусть будет по опыту
                    comparator = Comparator.comparingInt(Vacancy::getExperience);
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private int extractIncomeValue(Vacancy vacancy) {
        String income = vacancy.getIncomeLevel();
        if (income == null) {
            return Integer.MAX_VALUE;
        }

        String trimmed = income.trim();

        if ("договорная".equalsIgnoreCase(trimmed)) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public List<Vacancy> getSearched(String title){
        return vacancyRepository.findByTitleContainingIgnoreCase(title);
    }

    public Optional<Vacancy> findById(Long id) {
        return vacancyRepository.findById(id);
    }
}
