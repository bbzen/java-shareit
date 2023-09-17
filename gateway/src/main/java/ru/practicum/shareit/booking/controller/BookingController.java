package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestBody BookingInputDto requestDto) {
        bookingService.checkInputBooking(requestDto);
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(
            @RequestHeader(value = SHARER_USER_ID_HEADER) Long ownerUserId,
            @PathVariable Long bookingId,
            @RequestParam(value = "approved") Boolean isApproved) {
        return bookingClient.updateBooking(ownerUserId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBookerId(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = bookingService.checkStateParam(stateParam);
        bookingService.checkGetParams(from, size);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBySharerUserId(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = bookingService.checkStateParam(stateParam);
        bookingService.checkGetParams(from, size);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllBySharerUserId(userId, state, from, size);
    }
}