package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader(value = SHARER_USER_ID_HEADER) Long bookingUserId, @RequestBody BookingInputDto dto) {
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
    public List<Booking> findAllByBookerId(@RequestHeader(value = SHARER_USER_ID_HEADER) Long userId, @RequestParam(required = false) String state, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        return bookingService.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> findAllBySharerUserId(@RequestHeader(value = SHARER_USER_ID_HEADER) Long userId, @RequestParam(required = false) String state, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        return bookingService.findAllBySharerUserId(userId, state, from, size);
    }
}
