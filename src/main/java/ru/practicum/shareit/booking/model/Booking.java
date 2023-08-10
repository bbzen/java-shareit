package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long booker;
    private BookingStatus status;
}
