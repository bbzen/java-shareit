package ru.practicum.shareit.booking.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
