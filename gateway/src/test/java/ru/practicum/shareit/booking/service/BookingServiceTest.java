package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.exception.model.BookingBadRequestException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingServiceTest {
    private BookingService bookingService;
    private BookingInputDto bookingInputDto;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        bookingInputDto = new BookingInputDto(1L, start, end);
    }

    @Test
    public void checkInputBookingNormal() {
        bookingService.checkInputBooking(bookingInputDto);
    }

    @Test
    public void checkInputBookingFailStartNull() {
        bookingInputDto.setStart(null);
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Не задана дата начала бронирования.", thrown.getMessage());
    }

    @Test
    public void checkInputBookingFailEndNull() {
        bookingInputDto.setEnd(null);
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Не задана дата окончания бронирования.", thrown.getMessage());
    }

    @Test
    public void checkInputBookingFailEndInThePast() {
        bookingInputDto.setEnd(LocalDateTime.now().minusDays(1));
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Дата окончания бронирования не может быть раньше текущей даты.", thrown.getMessage());
    }

    @Test
    public void checkInputBookingFailEndBeforeStart() {
        bookingInputDto.setEnd(LocalDateTime.now().plusHours(1));
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Дата окончания бронирования не может быть раньше даты начала.", thrown.getMessage());
    }

    @Test
    public void checkInputBookingFailEndEqualsStart() {
        LocalDateTime src = LocalDateTime.now().plusDays(1);
        bookingInputDto.setEnd(src);
        bookingInputDto.setStart(src);
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Даты начала и окончания бронирования не могут быть равны.", thrown.getMessage());
    }

    @Test
    public void checkInputBookingFailStartBeforeNow() {
        bookingInputDto.setStart(LocalDateTime.now().minusDays(1));
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkInputBooking(bookingInputDto));
        assertEquals("Дата начала бронирования не может быть раньше текущей даты.", thrown.getMessage());
    }

    @Test
    public void checkGetParamsNormal() {
        Integer from = 5;
        Integer size = 10;
        bookingService.checkGetParams(from, size);
    }

    @Test
    public void checkGetParamsNulls() {
        Integer from = null;
        Integer size = null;
        bookingService.checkGetParams(from, size);
    }

    @Test
    public void checkGetParamsFromIsNegative() {
        Integer from = -1;
        Integer size = 10;

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkGetParams(from, size));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void checkGetParamsSizeIsZero() {
        Integer from = 5;
        Integer size = 0;

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.checkGetParams(from, size));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void checkStateParam() {
    }

    @Test
    public void checkUpdateParams() {
    }
}