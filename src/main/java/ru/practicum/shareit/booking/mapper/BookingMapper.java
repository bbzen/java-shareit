package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingShort;

public class BookingMapper {
    public static Booking toBooking(BookingDto dto) {
        return new Booking(dto.getStart(), dto.getEnd());
    }

    public static BookingShort toBookingShort(Booking booking) {
        return new BookingShort(booking.getId(), booking.getBooker().getId());
    }
}
