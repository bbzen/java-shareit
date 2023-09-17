package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingInputDto;

@Service
public interface BookingService {
    void checkInputBooking(BookingInputDto dto);

    void checkGetParams(Integer from, Integer size);

    void checkUpdateParams(Long ownerUserId, Long bookingId, Boolean approvalState);
}
