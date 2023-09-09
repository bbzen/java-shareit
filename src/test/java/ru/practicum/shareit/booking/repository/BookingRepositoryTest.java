package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User sharerUser;
    private User bookerUser;
    private Item itemSrc;
    private Booking bookingSrc;

    @BeforeEach
    public void setUp() {
        sharerUser = new User("sharerUserName", "sharer@mail.ru");
        sharerUser = userRepository.save(sharerUser);
        bookerUser = new User("bookerUserName", "booker@mail.ru");
        bookerUser = userRepository.save((bookerUser));
        itemSrc = new Item(1L, sharerUser.getId(), "itemOne", "itemOneDescription", true, null);
        itemSrc = itemRepository.save(itemSrc);
        bookingSrc = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemSrc, bookerUser, BookingStatus.WAITING);
    }

    @Test
    public void findAllByBookerIdAllTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllByBookerIdAll(bookerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllByBookerAndStatusTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllByBookerAndStatus(bookerUser.getId(), "WAITING");

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllByBookerFutureTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllByBookerFuture(bookerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllByBookerPastTest() {
        bookingSrc.setEnd(LocalDateTime.now().minusDays(1));
        bookingSrc.setStart(LocalDateTime.now().minusDays(3));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllByBookerPast(bookerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllByBookerCurrentTest() {
        bookingSrc.setEnd(LocalDateTime.now().plusDays(1));
        bookingSrc.setStart(LocalDateTime.now().minusDays(3));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllByBookerCurrent(bookerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllBySharerUserId(sharerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdPageTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        Pageable page = PageRequest.of(0, 5);
        List<Booking> resultList = bookingRepository.findAllBySharerUserId(sharerUser.getId(), page);

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdFutureTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllBySharerUserIdFuture(sharerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdPast() {
        bookingSrc.setEnd(LocalDateTime.now().minusDays(1));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllBySharerUserIdFuture(sharerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdAndStatusTest() {
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllBySharerUserIdAndStatus(sharerUser.getId(), "WAITING");

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findAllBySharerUserIdCurrentTest() {
        bookingSrc.setEnd(LocalDateTime.now().plusDays(1));
        bookingSrc.setStart(LocalDateTime.now().minusDays(3));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.findAllBySharerUserIdCurrent(sharerUser.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }

    @Test
    public void findNextBookingTest() {
        bookingSrc.setStatus(BookingStatus.APPROVED);
        bookingSrc.setStart(LocalDateTime.now().minusDays(1));
        bookingSrc.setStart(LocalDateTime.now().plusDays(1));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        Booking nextBooking = bookingRepository.findNextBooking(itemSrc.getId());

        assertEquals(resultBooking.getId(), nextBooking.getId());
        assertEquals(resultBooking.getStart(), nextBooking.getStart());
        assertEquals(resultBooking.getEnd(), nextBooking.getEnd());
        assertEquals(resultBooking.getItem(), nextBooking.getItem());
        assertEquals(resultBooking.getBooker(), nextBooking.getBooker());
    }

    @Test
    public void findLastBookingTest() {
        bookingSrc.setStatus(BookingStatus.APPROVED);
        bookingSrc.setStart(LocalDateTime.now().minusDays(1));
        bookingSrc.setEnd(LocalDateTime.now().plusDays(3));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        Booking nextBooking = bookingRepository.findLastBooking(itemSrc.getId());

        assertEquals(resultBooking.getId(), nextBooking.getId());
        assertEquals(resultBooking.getStart(), nextBooking.getStart());
        assertEquals(resultBooking.getEnd(), nextBooking.getEnd());
        assertEquals(resultBooking.getItem(), nextBooking.getItem());
        assertEquals(resultBooking.getBooker(), nextBooking.getBooker());
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusApprovedTest() {
        bookingSrc.setStatus(BookingStatus.APPROVED);
        bookingSrc.setEnd(LocalDateTime.now().minusDays(1));
        Booking resultBooking = bookingRepository.save(bookingSrc);
        List<Booking> resultList = bookingRepository.existsByBookerIdAndItemIdAndStatusApproved(bookerUser.getId(), itemSrc.getId());

        assertEquals(resultBooking.getId(), resultList.get(0).getId());
        assertEquals(resultBooking.getStart(), resultList.get(0).getStart());
        assertEquals(resultBooking.getEnd(), resultList.get(0).getEnd());
        assertEquals(resultBooking.getItem(), resultList.get(0).getItem());
        assertEquals(resultBooking.getBooker(), resultList.get(0).getBooker());
    }
}