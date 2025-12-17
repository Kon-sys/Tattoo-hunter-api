package com.example.chatservice.service;

import com.example.chatservice.model.SenderRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    public String getCompanyName(Long companyId) {
        try {
            return (String) em.createNativeQuery("""
                    SELECT c.name
                    FROM company c
                    WHERE c.id = :companyId
                    """)
                    .setParameter("companyId", companyId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Company not found: id=" + companyId);
        }
    }

    // Вариант: с проверкой, что вакансия принадлежит companyId (рекомендую для контроля доступа)
    public String getVacancyName(Long companyId, Long vacancyId) {
        try {
            return (String) em.createNativeQuery("""
                    SELECT v.title
                    FROM vacancy v
                    WHERE v.id = :vacancyId
                      AND v.company_id = :companyId
                    """)
                    .setParameter("vacancyId", vacancyId)
                    .setParameter("companyId", companyId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException(
                    "Vacancy not found or not belongs to company: vacancyId=" + vacancyId + ", companyId=" + companyId
            );
        }
    }

    public String getEmployeeName(String login) {
        try {
            Object[] row = (Object[]) em.createNativeQuery("""
                    SELECT e.first_name, e.last_name
                    FROM employee e
                    JOIN t_user u ON u.id = e.user_id
                    WHERE u.login = :login
                    """)
                    .setParameter("login", login)
                    .getSingleResult();

            String first = row[0] == null ? "" : row[0].toString().trim();
            String last  = row[1] == null ? "" : row[1].toString().trim();

            String full = (first + " " + last).trim();
            return full.isEmpty() ? "(no name)" : full;

        } catch (NoResultException e) {
            throw new IllegalArgumentException("Employee not found by login=" + login);
        }
    }

    public String getSenderName(String login, SenderRole senderRole) {
        if (login == null || login.isBlank()) return "(no name)";

        try {
            if (senderRole == SenderRole.COMPANY) {
                // company.name по user_id компании
                Object nameObj = em.createNativeQuery("""
                    SELECT c.name
                    FROM company c
                    JOIN t_user u ON u.id = c.user_id
                    WHERE u.login = :login
                    """)
                        .setParameter("login", login)
                        .getSingleResult();

                String name = nameObj == null ? "" : nameObj.toString().trim();
                return name.isEmpty() ? "(no name)" : name;
            }

            // EMPLOYEE (и всё остальное считаем как employee)
            Object[] row = (Object[]) em.createNativeQuery("""
                SELECT e.first_name, e.last_name
                FROM employee e
                JOIN t_user u ON u.id = e.user_id
                WHERE u.login = :login
                """)
                    .setParameter("login", login)
                    .getSingleResult();

            String first = row[0] == null ? "" : row[0].toString().trim();
            String last  = row[1] == null ? "" : row[1].toString().trim();

            String full = (first + " " + last).trim();
            return full.isEmpty() ? "(no name)" : full;

        } catch (NoResultException e) {
            String who = (senderRole == SenderRole.COMPANY) ? "Company" : "Employee";
            throw new IllegalArgumentException(who + " not found by login=" + login);
        }
    }

}