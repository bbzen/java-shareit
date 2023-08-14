package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader(value = SHARER_USER_ID_HEADER) Long bookingUserId, @RequestBody BookingDto dto) {
        return bookingService.create(bookingUserId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateStatus(@RequestHeader(value = SHARER_USER_ID_HEADER) Long ownerUserId, @PathVariable Long bookingId, @RequestParam(value = "approved") Boolean isApproved) {
        return bookingService.updateBooking(ownerUserId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public Booking findById(@RequestHeader(value = SHARER_USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> findAllByUserId(@RequestHeader(value = SHARER_USER_ID_HEADER) Long userId, @RequestParam(required = false) String state) {
        return bookingService.findAllByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> findAllBySharerUserId(@RequestHeader(value = SHARER_USER_ID_HEADER) Long userId, @RequestParam(required = false) String state) {
        return bookingService.findAllBySharerUserId(userId, state);
    }
}
