package com.example.authservice.service;

import com.example.authservice.dto.CountersDto;
import com.example.authservice.dto.UserInfoDto;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    public User registerUserWithRole(String login, String rawPassword, Role role) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(rawPassword);
        user.setRole(role);

        if(findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException("User with login " + login + " already exists");
        }

        user = userRepository.save(user);

        createRole(user.getLogin(), user.getRole());
        return user;
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void createRole(String login, Role role) {
        Optional<User> user = findByLogin(login);
        if(user.isEmpty())
            throw new IllegalArgumentException("User not found");
        Long userId = user.get().getId();

        if (role == Role.ROLE_EMPLOYEE) {
            String sql = """
                INSERT INTO employee (user_id)
                VALUES (?1)
                """;
            entityManager.createNativeQuery(sql)
                    .setParameter(1, userId)
                    .executeUpdate();

        } else if (role == Role.ROLE_COMPANY) {
            String sql = """
                INSERT INTO company (user_id)
                VALUES (?1)
                """;
            entityManager.createNativeQuery(sql)
                    .setParameter(1, userId)
                    .executeUpdate();
        }
    }

    public boolean isExistsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public UserInfoDto getUserInfoByLogin(String login) {

        Optional<User> userOpt = findByLogin(login);

        if(userOpt.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setRole(user.getRole().name());

        // 👉 тут как раз «привязка к employee/company»
        // Вариант 1: если в User есть связи:
        //  @OneToOne(mappedBy = "user")
        //  private Employee employee;
        //  private Company company;
        //
        // тогда:
        /*
        if (user.getEmployee() != null) {
            dto.setEmployeeId(user.getEmployee().getId());
        }
        if (user.getCompany() != null) {
            dto.setCompanyId(user.getCompany().getId());
        }
        */

        // Вариант 2: если этих связей пока нет — временно оставляем null
        dto.setEmployeeId(null);
        dto.setCompanyId(null);

        return dto;
    }

    private final JdbcTemplate jdbcTemplate;

    // ... остальные методы

    public CountersDto getCounters() {
        long employees = userRepository.countByRole(Role.ROLE_EMPLOYEE);
        long companies = userRepository.countByRole(Role.ROLE_COMPANY);

        Long vacancies = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vacancy", // <-- тут имя твоей таблицы
                Long.class
        );

        return new CountersDto(
                employees,
                companies,
                vacancies != null ? vacancies : 0L
        );
    }
}
