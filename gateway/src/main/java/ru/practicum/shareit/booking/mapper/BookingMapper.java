package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEntityDataKeeping;
import ru.practicum.shareit.booking.model.BookingInputDto;

public class BookingMapper {
    public static Booking toBooking(BookingInputDto dto) {
        return new Booking(dto.getStart(), dto.getEnd());
    }

    public static BookingEntityDataKeeping toBookingEntityDataKeeping(Booking booking) {
        return new BookingEntityDataKeeping(booking.getId(), booking.getBooker().getId());
    }

    public static BookingInputDto toBookingInputDto(Booking booking) {
        return new BookingInputDto(booking.getId(), booking.getStart(), booking.getEnd());
    }
}
