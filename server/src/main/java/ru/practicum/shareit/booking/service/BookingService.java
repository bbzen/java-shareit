package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;

import java.util.List;

@Service
public interface BookingService {

    Booking create(Long bookingUserId, BookingInputDto bookingInputDto);

    Booking updateBooking(Long ownerUserId, Long bookingId, Boolean approvalState);

    Booking findBooking(Long userId, Long bookingId);

    List<Booking> findAllByBookerId(Long userId, String state, Integer from, Integer size);

    List<Booking> findAllBySharerUserId(Long userId, String state, Integer from, Integer size);
}
