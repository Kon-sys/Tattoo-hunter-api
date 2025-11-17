package com.example.vacancyservice.exception;

public class NotFoundVacancyException extends RuntimeException {
    public NotFoundVacancyException(String message) {
        super(message);
    }
}
