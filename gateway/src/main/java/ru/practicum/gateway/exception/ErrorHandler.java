package ru.practicum.gateway.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(Exception exception) {
        return Map.of("error", exception.getMessage());
    }
}