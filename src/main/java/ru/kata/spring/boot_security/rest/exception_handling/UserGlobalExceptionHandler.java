package ru.kata.spring.boot_security.rest.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class UserGlobalExceptionHandler {

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<UserIncorrectData> handleNoSuchUserException(NoSuchUserException noSuchUserException) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(noSuchUserException.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserIncorrectData> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo("Ошибки валидации: " + errors.toString());
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<UserIncorrectData> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo("Некорректный тип данных: " + methodArgumentTypeMismatchException.getName() + " должен быть числом.");
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectEmailException.class)
    public ResponseEntity<UserIncorrectData> handleIncorrectEmailException(IncorrectEmailException incorrectEmailException) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(incorrectEmailException.getMessage());
        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserIncorrectData> handleException(Exception exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo("Произошла ошибка: " + exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleNoContentException(NoContentException noContentException) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(noContentException.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NO_CONTENT);
    }
}

