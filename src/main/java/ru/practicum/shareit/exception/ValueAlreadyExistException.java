package ru.practicum.shareit.exception;

public class ValueAlreadyExistException extends RuntimeException {
    public ValueAlreadyExistException(String message) {
        super(message);
    }
}
