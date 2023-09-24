package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private User userOne;
    private User userTwo;

    @BeforeEach
    public void initial() {
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
    }

    @Test
    public void findUser() throws Exception {
        when(userService.findUser(Mockito.anyLong()))
                .thenReturn(userOne);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(userOne.getName())))
                .andExpect(jsonPath("email", is(userOne.getEmail())))
                .andReturn();
    }

    @Test
    public void findAllUsers() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(List.of(userOne, userTwo));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(userTwo.getId()), Long.class))
                .andExpect(jsonPath("[1].name", is(userTwo.getName())))
                .andExpect(jsonPath("[1].email", is(userTwo.getEmail())))
                .andReturn();
    }

    @Test
    public void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userOne);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserMapper.toUserDto(userOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(userOne.getName())))
                .andExpect(jsonPath("email", is(userOne.getEmail())))
                .andReturn();
    }

    @Test
    public void updateUser() throws Exception {
        when(userService.updateUser(Mockito.anyLong(), any(UserDto.class)))
                .thenReturn(userOne);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(UserMapper.toUserDto(userOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(userOne.getName())))
                .andExpect(jsonPath("email", is(userOne.getEmail())))
                .andReturn();

        userOne.setName("updatedName");
        userOne.setEmail("updatedEmail");

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(UserMapper.toUserDto(userOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(userOne.getName())))
                .andExpect(jsonPath("email", is(userOne.getEmail())))
                .andReturn();
    }

    @Test
    public void removeUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }
}