package com.example.listingvacanciesservice.service;

import com.example.listingvacanciesservice.dto.VacancyFilter;
import com.example.listingvacanciesservice.model.Vacancy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacancyFilterService {

    public List<Vacancy> filterVacancies(List<Vacancy> vacancies, VacancyFilter filter) {
        if (filter == null) {
            return vacancies;
        }

        return vacancies.stream()
                // 1. Фильтр по income (договорная / от X)
                .filter(v -> matchesIncome(v, filter.getIncome()))
                // 2. Фильтр по Busy
                .filter(v -> filter.getBusy() == null || v.getBusy() == filter.getBusy())
                // 3. Фильтр по WorkSchedule
                .filter(v -> filter.getWorkSchedule() == null || v.getWorkSchedule() == filter.getWorkSchedule())
                // 4. Фильтр по WorkType
                .filter(v -> filter.getWorkType() == null || v.getWorkType() == filter.getWorkType())
                // 5. Фильтр по опыту
                .filter(v -> filter.getMinExperience() == null || v.getExperience() >= filter.getMinExperience())
                .filter(v -> filter.getMaxExperience() == null || v.getExperience() <= filter.getMaxExperience())
                // 6. Фильтр по рабочим часам
                .filter(v -> filter.getMinWorkingHours() == null || v.getWorkingHours() >= filter.getMinWorkingHours())
                .filter(v -> filter.getMaxWorkingHours() == null || v.getWorkingHours() <= filter.getMaxWorkingHours())
                // 7. Фильр по имени компании
                .filter(v -> filter.getCompanyName() == null
                        || (v.getCompany() != null
                        && v.getCompany().getName() != null
                        && v.getCompany().getName().equalsIgnoreCase(filter.getCompanyName())))
                .collect(Collectors.toList());
    }

    /**
     * Логика по income:
     * - если income == null -> не фильтруем по доходу
     * - если income == "NEGOTIABLE" -> оставляем только вакансии с договорной
     * - если income == число -> оставляем все, у кого числовой incomeLevel >= этого числа
     */
    private boolean matchesIncome(Vacancy vacancy, String incomeFilter) {
        if (incomeFilter == null || incomeFilter.isBlank()) {
            return true; // не фильтруем по доходу
        }

        String vacancyIncome = vacancy.getIncomeLevel();
        if (vacancyIncome == null || vacancyIncome.isBlank()) {
            return false;
        }

        String trimmedFilter = incomeFilter.trim();

        // случай "договорная"
        if ("NEGOTIABLE".equalsIgnoreCase(trimmedFilter)) {
            return "NEGOTIABLE".equalsIgnoreCase(vacancyIncome.trim());
        }

        // случай числового порога
        int minIncome;
        try {
            minIncome = Integer.parseInt(trimmedFilter);
        } catch (NumberFormatException e) {
            // если в фильтре пришла фигня — просто не матчим ничего
            return false;
        }

        // если у вакансии "договорная" — она не подходит под числовой фильтр
        if ("NEGOTIABLE".equalsIgnoreCase(vacancyIncome.trim())) {
            return false;
        }

        // пробуем распарсить income вакансии как число
        try {
            int vacancyIncomeValue = Integer.parseInt(vacancyIncome.trim());
            return vacancyIncomeValue >= minIncome;
        } catch (NumberFormatException e) {
            // если в БД лежит мусор типа "5000 руб" — не матчим
            return false;
        }
    }
}

