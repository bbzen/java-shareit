package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingState;

@Service
public interface BookingService {
    void checkInputBooking(BookingInputDto dto);

    void checkGetParams(Integer from, Integer size);
    BookingState checkStateParam(String stateParam);

    void checkUpdateParams(Long ownerUserId, Long bookingId, Boolean approvalState);
}
