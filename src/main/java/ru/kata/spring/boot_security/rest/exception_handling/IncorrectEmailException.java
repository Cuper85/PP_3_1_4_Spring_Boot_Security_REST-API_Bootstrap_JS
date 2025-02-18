package ru.kata.spring.boot_security.rest.exception_handling;

public class IncorrectEmailException extends RuntimeException {

    public IncorrectEmailException(String message) {
        super(message);
    }
}
