package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DirtiesContext
    public void createBookingNormal() {
        UserDto sharerUserDto = generator.nextObject(UserDto.class);
        sharerUserDto.setEmail("sharer@mail.ru");
        User sharerUser = userService.createUser(sharerUserDto);
        UserDto bookerUserDto = generator.nextObject(UserDto.class);
        bookerUserDto.setEmail("booker@mail.ru");
        User bookerUser = userService.createUser(bookerUserDto);
        Item srcItem = itemService.create(sharerUser.getId(), generator.nextObject(ItemInputDto.class));
        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(srcItem.getId());
        bookingInputDto.setStart(LocalDateTime.now().plusDays(1));
        bookingInputDto.setEnd(LocalDateTime.now().plusDays(3));
        Booking result = bookingService.create(bookerUser.getId(), bookingInputDto);
        assertEquals(bookingInputDto.getStart(), result.getStart());
        assertEquals(bookingInputDto.getEnd(), result.getEnd());
        assertEquals(srcItem.getId(), result.getItem().getId());
        assertEquals(srcItem.getName(), result.getItem().getName());
        assertEquals(bookerUser.getId(), result.getBooker().getId());
        assertEquals(bookerUser.getName(), result.getBooker().getName());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }
}