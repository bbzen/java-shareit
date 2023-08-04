package ru.practicum.shareit.exception.model;

public class UserEmailException extends RuntimeException {
    public UserEmailException(String message) {
        super(message);
    }
}
