package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Booking create(Long bookingUserId, BookingInputDto bookingInputDto) {
        checkBookingDtoDate(bookingInputDto);
        Item currentItem = itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("Предмета " + bookingInputDto.getItemId() + " нет в базе."));
        if (bookingUserId.equals(currentItem.getSharerUserId())) {
            log.debug("Владелец предмета не может его забронировать.");
            throw new BookingNotFoundException("Владелец предмета не может его забронировать.");
        }
        if (currentItem.getAvailable()) {
            Booking current = BookingMapper.toBooking(bookingInputDto);
            current.setBooker(userRepository.findById(bookingUserId).orElseThrow(() ->
                    new UserNotFoundException("Пользователь " + bookingUserId + " не найден")));
            current.setItem(currentItem);
            current.setStatus(BookingStatus.WAITING);
            return bookingRepository.save(current);
        }
        log.debug("Предмет " + currentItem.getId() + " не доступен для бронирования.");
        throw new ItemInvalidException("Предмет " + currentItem.getId() + " не доступен для бронирования.");
    }

    @Override
    public Booking updateBooking(Long ownerUserId, Long bookingId, Boolean approvalState) {
        checkUpdateParams(ownerUserId, bookingId, approvalState);
        Booking currentBooking = getBookingById(bookingId);
        if (ownerUserId.equals(currentBooking.getItem().getSharerUserId())) {
            if (approvalState) {
                if (currentBooking.getStatus() != BookingStatus.APPROVED) {
                    currentBooking.setStatus(BookingStatus.APPROVED);
                } else {
                    throw new BookingBadRequestException("Запрос уже одобрен.");
                }
            } else {
                currentBooking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(currentBooking);
            return currentBooking;
        }
        log.debug("Указанный в запросе предмет не принадлежит указанному пользователю.");
        throw new BookingNotFoundException("Указанный в запросе предмет не принадлежит указанному пользователю.");
    }

    @Override
    public Booking findBooking(Long userId, Long bookingId) {
        Booking currentBooking = getBookingById(bookingId);
        if (currentBooking.getBooker().getId().equals(userId) || currentBooking.getItem().getSharerUserId().equals(userId)) {
            return currentBooking;
        }
        log.debug("Указанная в запросе заявка на бронирование не принадлежит указанному пользователю.");
        throw new BookingNotFoundException("Указанная в запросе заявка на бронирование не принадлежит указанному пользователю.");
    }

    @Override
    public List<Booking> findAllByBookerId(Long userId, String state, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            if (state == null || state.equals("ALL")) {
                if (from == null || size == null) {
                    return bookingRepository.findAllByBookerIdAll(userId);
                }
                Sort sortById = Sort.by(Sort.Direction.DESC, "id");
                Pageable page = PageRequest.of(Math.abs(from / size), size, sortById);
                return bookingRepository.findAllByBookerId(userId, page);
            }
            if (state.equalsIgnoreCase("FUTURE")) {
                return bookingRepository.findAllByBookerFuture(userId);
            }
            if (state.equalsIgnoreCase("PAST")) {
                return bookingRepository.findAllByBookerPast(userId);
            }
            if (state.equals("WAITING") || state.equals("REJECTED")) {
                return bookingRepository.findAllByBookerAndStatus(userId, state);
            }
            if (state.equals("CURRENT")) {
                return bookingRepository.findAllByBookerCurrent(userId);
            }
            throw new BookingBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Пользователь с ID " + userId + " не найден.");
        throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
    }

    @Override
    public List<Booking> findAllBySharerUserId(Long userId, String state, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            if (state == null || state.equals("ALL")) {
                if (from == null || size == null) {
                    return bookingRepository.findAllBySharerUserId(userId);
                }
                Pageable page = PageRequest.of(Math.abs(from / size), size);
                return bookingRepository.findAllBySharerUserId(userId, page);
            }
            if (state.equalsIgnoreCase("FUTURE")) {
                return bookingRepository.findAllBySharerUserIdFuture(userId);
            }
            if (state.equalsIgnoreCase("PAST")) {
                return bookingRepository.findAllBySharerUserIdPast(userId);
            }
            if (state.equals("WAITING") || state.equals("REJECTED")) {
                return bookingRepository.findAllBySharerUserIdAndStatus(userId, state);
            }
            if (state.equals("CURRENT")) {
                return bookingRepository.findAllBySharerUserIdCurrent(userId);
            }
            throw new BookingBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Пользователь с ID " + userId + " не найден.");
        throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
    }

    private void checkBookingDtoDate(BookingInputDto dto) {
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
    }

    private void checkUpdateParams(Long ownerUserId, Long bookingId, Boolean approvalState) {
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
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ItemNotFoundException("Заявки на бронирование " + bookingId + " нет в базе."));
    }
}

