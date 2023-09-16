package ru.practicum.shareit.exception.model;

public class ItemInvalidException extends RuntimeException {
    public ItemInvalidException(String message) {
        super(message);
    }
}
