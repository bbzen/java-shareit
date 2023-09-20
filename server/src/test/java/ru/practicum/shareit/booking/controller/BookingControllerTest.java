package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Booking bookingOne;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
        itemOne = new Item(1L, 1L, "ItemOne", "ItemDescription", true, null);
        bookingOne = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), itemOne, userOne, BookingStatus.WAITING);
    }

    @Test
    public void create() throws Exception {
        when(bookingService.create(Mockito.anyLong(), Mockito.any(BookingInputDto.class)))
                .thenReturn(bookingOne);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(BookingMapper.toBookingInputDto(bookingOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("status", is(BookingStatus.WAITING.toString())))
                .andReturn();
    }

    @Test
    public void updateStatus() throws Exception {
        when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingOne);

        mvc.perform(patch("/bookings/{bookingId}", 1L).header("X-Sharer-User-Id", 1).param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("status", is(BookingStatus.WAITING.toString())))
                .andReturn();

        bookingOne.setStatus(BookingStatus.REJECTED);

        mvc.perform(patch("/bookings/{bookingId}", 1L).header("X-Sharer-User-Id", 1).param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("status", is(BookingStatus.REJECTED.toString())))
                .andReturn();
    }

    @Test
    public void findById() throws Exception {
        when(bookingService.findBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingOne);

        mvc.perform(get("/bookings/{bookingId}", 1L).header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("item.id", is(Math.toIntExact(bookingOne.getItem().getId()))))
                .andExpect(jsonPath("booker.id", is(Math.toIntExact(bookingOne.getBooker().getId()))))
                .andExpect(jsonPath("status", is(bookingOne.getStatus().toString())))
                .andReturn();
    }

    @Test
    public void findAllByBookerId() throws Exception {
        when(bookingService.findAllByBookerId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingOne));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state","ALL")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(Math.toIntExact(bookingOne.getItem().getId()))))
                .andExpect(jsonPath("$[0].booker.id", is(Math.toIntExact(bookingOne.getBooker().getId()))))
                .andExpect(jsonPath("$[0].status", is(bookingOne.getStatus().toString())))
                .andReturn();
    }

    @Test
    public void findAllBySharerUserId() throws Exception {
        when(bookingService.findAllBySharerUserId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingOne));

        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1).param("state","ALL").param("from", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOne.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(Math.toIntExact(bookingOne.getItem().getId()))))
                .andExpect(jsonPath("$[0].booker.id", is(Math.toIntExact(bookingOne.getBooker().getId()))))
                .andExpect(jsonPath("$[0].status", is(bookingOne.getStatus().toString())))
                .andReturn();
    }
}