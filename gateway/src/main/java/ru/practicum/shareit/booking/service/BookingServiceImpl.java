package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.exception.model.BookingBadRequestException;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    public Boolean isBookingValid(BookingInputDto dto) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (start == null) {
            log.debug("Не задана дата начала бронирования.");
            throw new BookingBadRequestException("Не задана дата начала бронирования.");
        }
        if (end == null) {
            log.debug("Не задана дата окончания бронирования.");
            throw new BookingBadRequestException("Не задана дата окончания бронирования.");
        }
        if (end.isBefore(now)) {
            log.debug("Дата окончания бронирования не может быть раньше текущей даты.");
            throw new BookingBadRequestException("Дата окончания бронирования не может быть раньше текущей даты.");
        }
        if (end.isBefore(start)) {
            log.debug("Дата окончания бронирования не может быть раньше даты начала.");
            throw new BookingBadRequestException("Дата окончания бронирования не может быть раньше даты начала.");
        }
        if (start.equals(end)) {
            log.debug("Даты начала и окончания бронирования не могут быть равны.");
            throw new BookingBadRequestException("Даты начала и окончания бронирования не могут быть равны.");
        }
        if (start.isBefore(now)) {
            log.debug("Дата начала бронирования не может быть раньше текущей даты.");
            throw new BookingBadRequestException("Дата начала бронирования не может быть раньше текущей даты.");
        }
        return true;
    }

    public Boolean isUpdateParamsValid(Long ownerUserId, Long bookingId, Boolean approvalState) {
        if (approvalState == null) {
            log.debug("В запросе нет статуса заявки бронирования.");
            throw new BookingBadRequestException("В запросе нет статуса заявки бронирования.");
        }
        if (ownerUserId == null) {
            log.debug("В запросе нет ID пользователя.");
            throw new BookingBadRequestException("В запросе нет ID Пользователя.");
        }
        if (bookingId == null) {
            log.debug("В запросе нет ID заявки на бронирование.");
            throw new BookingBadRequestException("В запросе нет ID заявки на бронирование.");
        }
        return true;
    }
}

