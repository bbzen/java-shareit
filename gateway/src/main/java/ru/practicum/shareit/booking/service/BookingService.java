package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingInputDto;

@Service
public interface BookingService {
    Boolean isBookingValid(BookingInputDto dto);

    Boolean isUpdateParamsValid(Long ownerUserId, Long bookingId, Boolean approvalState);
}
