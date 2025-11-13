package com.example.authservice.service;

import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            user = userRepository.save(user); //DataIntegrityViolationException
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with this login already exists");
        }

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
}
