package ru.practicum.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ValueAlreadyExistException extends RuntimeException {
    public ValueAlreadyExistException(String message) {
        super(message);
    }
}
