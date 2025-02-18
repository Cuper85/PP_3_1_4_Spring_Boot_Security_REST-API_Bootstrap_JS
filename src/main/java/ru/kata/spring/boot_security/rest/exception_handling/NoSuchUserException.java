package ru.kata.spring.boot_security.rest.exception_handling;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(String message) {
        super(message);
    }
}
