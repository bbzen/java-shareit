package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;

@Service
public interface BookingService {

    Booking create(Long bookingUserId, BookingDto bookingDto);

    Booking updateBooking(Long ownerUserId, Long bookingId, Boolean approvalState);

    Booking findBooking(Long userId, Long bookingId);

    List<Booking> findAllByBookerId(Long userId, String state);

    List<Booking> findAllBySharerUserId(Long userId, String state);
}
