package ru.practicum.shareit.exception.model;

public class ItemRequestBadRequestException extends RuntimeException {
    public ItemRequestBadRequestException(String message) {
        super(message);
    }
}
