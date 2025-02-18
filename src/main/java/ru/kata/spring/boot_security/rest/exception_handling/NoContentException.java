package ru.kata.spring.boot_security.rest.exception_handling;

public class NoContentException extends RuntimeException {

    public NoContentException(String message) {
        super(message);
    }
}
