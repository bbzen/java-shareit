package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingService bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Booking bookingOne;

    @BeforeEach
    public void setUp() {
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
        itemOne = new Item(1L, 1L, "ItemOne", "ItemDescription", true, null);
        bookingOne = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemOne, userTwo, BookingStatus.WAITING);
    }

    @Test
    public void testCreateNormal() {
        when(bookingRepository.save(any()))
                .thenReturn(bookingOne);
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));

        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);
        Booking result = bookingService.create(userTwo.getId(), bookingInputDto);

        assertEquals(bookingOne.getId(), result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testCreateOwnerIsBookerFail() {
        bookingOne.setBooker(userOne);
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingNotFoundException.class, () -> bookingService.create(userOne.getId(), bookingInputDto));
        assertEquals("Владелец предмета не может его забронировать.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateNoItemInDbFail() {
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);
        Exception thrown = assertThrows(ItemNotFoundException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Предмета 1 нет в базе.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateStartDateIsBeforeTodayFail() {
        bookingOne.setStart(LocalDateTime.now().minusDays(1));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Дата начала бронирования не может быть раньше текущей даты.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateNoStartDateFail() {
        bookingOne.setStart(null);
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Не задана дата начала бронирования.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateNoEndDateFail() {
        bookingOne.setEnd(null);
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Не задана дата окончания бронирования.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateEndDateIsBeforeTodayFail() {
        bookingOne.setEnd(LocalDateTime.now().minusDays(1));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Дата окончания бронирования не может быть раньше текущей даты.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateEndDateIsBeforeStartFail() {
        bookingOne.setEnd(LocalDateTime.now().plusDays(1));
        bookingOne.setStart(LocalDateTime.now().plusDays(3));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Дата окончания бронирования не может быть раньше даты начала.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateEndAndStartTheSameFail() {
        LocalDateTime src = LocalDateTime.now().plusDays(1);
        bookingOne.setEnd(src);
        bookingOne.setStart(src);
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Даты начала и окончания бронирования не могут быть равны.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateNoBookerUserInDbFail() {
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Пользователь " + userTwo.getId() + " не найден", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateItemNotAvailableFail() {
        itemOne.setAvailable(false);
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        BookingInputDto bookingInputDto = BookingMapper.toBookingInputDto(bookingOne);

        Exception thrown = assertThrows(ItemInvalidException.class, () -> bookingService.create(userTwo.getId(), bookingInputDto));

        assertEquals("Предмет " + itemOne.getId() + " не доступен для бронирования.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingNormalApproved() {
        when(bookingRepository.save(any()))
                .thenReturn(bookingOne);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Booking result = bookingService.updateBooking(userOne.getId(), bookingOne.getId(), true);

        assertEquals(bookingOne.getId(), result.getId());
        assertEquals(bookingOne.getStatus(), result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void updateBookingNormalRejected() {
        when(bookingRepository.save(any()))
                .thenReturn(bookingOne);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Booking result = bookingService.updateBooking(userOne.getId(), bookingOne.getId(), false);
        bookingOne.setStatus(BookingStatus.REJECTED);

        assertEquals(bookingOne.getId(), result.getId());
        assertEquals(bookingOne.getStatus(), result.getStatus());
    }

    @Test
    public void updateBookingNotOwner() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Exception thrown = assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking(userTwo.getId(), bookingOne.getId(), true));

        assertEquals("Указанный в запросе предмет не принадлежит указанному пользователю.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingAlreadyApproved() {
        bookingOne.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.updateBooking(userOne.getId(), bookingOne.getId(), true));

        assertEquals("Запрос уже одобрен.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingNoBookingId() {
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.updateBooking(userOne.getId(), null, true));

        assertEquals("В запросе нет ID заявки на бронирование.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingNoApprovalState() {
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.updateBooking(userOne.getId(), bookingOne.getId(), null));

        assertEquals("В запросе нет статуса заявки бронирования.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingNoOwnerUserId() {
        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.updateBooking(null, bookingOne.getId(), true));

        assertEquals("В запросе нет ID Пользователя.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void updateBookingTryToGetAbsentBooking() {
        Exception thrown = assertThrows(ItemNotFoundException.class, () -> bookingService.updateBooking(userOne.getId(), bookingOne.getId(), true));

        assertEquals("Заявки на бронирование " + bookingOne.getId() + " нет в базе.", thrown.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void findBookingNormal() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Booking result = bookingService.findBooking(userTwo.getId(), bookingOne.getId());
        assertEquals(bookingOne.getId(), result.getId());
    }

    @Test
    public void findBookingUserNotOwner() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(bookingOne));

        Exception thrown = assertThrows(BookingNotFoundException.class, () -> bookingService.findBooking(3L, bookingOne.getId()));
        assertEquals("Указанная в запросе заявка на бронирование не принадлежит указанному пользователю.", thrown.getMessage());
    }

    @Test
    public void findAllByBookerIdStateAllNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), any()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "ALL", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateNullNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), any()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), null, 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateNullParamsNullNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerIdAll(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), null, null, null);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateFutureNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerFuture(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "FUTURE", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStatePastNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerPast(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "PAST", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateWaitingNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerAndStatus(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "WAITING", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateRejectedNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerAndStatus(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "REJECTED", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdStateCurrentNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerCurrent(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllByBookerId(userTwo.getId(), "CURRENT", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByBookerIdUserNotFoundFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> bookingService.findAllByBookerId(userTwo.getId(), "ALL", 0, 5));
        assertEquals("Пользователь с ID " + userTwo.getId() + " не найден.", thrown.getMessage());
    }

    @Test
    public void findAllByBookerIdInvalidFromParamFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllByBookerId(userTwo.getId(), "ALL", -1, 5));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void findAllByBookerIdInvalidSizeParamFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllByBookerId(userTwo.getId(), "ALL", 0, 0));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void findAllByBookerIdUnsupportedStatusFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllByBookerId(userTwo.getId(), "UNSUPPORTED", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }

    @Test
    public void findAllBySharerUserIdNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserId(Mockito.anyLong(), any()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "ALL", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateNullNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserId(Mockito.anyLong(), any()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), null, 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateNullParamsNullNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserId(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), null, null, null);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateFutureNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserIdFuture(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "FUTURE", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStatePastNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserIdPast(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "PAST", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateWaitingNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserIdAndStatus(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "WAITING", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateRejectedNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserIdAndStatus(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "REJECTED", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdStateCurrentNormal() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBySharerUserIdCurrent(Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));

        List<Booking> result = bookingService.findAllBySharerUserId(userOne.getId(), "CURRENT", 0, 5);
        assertEquals(bookingOne.getId(), result.get(0).getId());
    }

    @Test
    public void findAllBySharerIdUserNotFoundFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> bookingService.findAllBySharerUserId(userOne.getId(), "ALL", 0, 5));
        assertEquals("Пользователь с ID " + userOne.getId() + " не найден.", thrown.getMessage());
    }

    @Test
    public void findAllBySharerIdInvalidFromParamFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllBySharerUserId(userOne.getId(), "ALL", -1, 5));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void findAllBySharerIdInvalidSizeParamFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllBySharerUserId(userOne.getId(), "ALL", 0, 0));
        assertEquals("Не верно заданы параметры поиска бронирования.", thrown.getMessage());
    }

    @Test
    public void findAllBySharerIdUnsupportedStatusFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(BookingBadRequestException.class, () -> bookingService.findAllBySharerUserId(userOne.getId(), "UNSUPPORTED", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }
}