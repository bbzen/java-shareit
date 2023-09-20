package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.UserInvalidDataException;
import ru.practicum.shareit.user.model.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    public void checkUserDataCreateNormal() {
        UserDto userSrc = new UserDto("OwnerUser", "user@email.ru");
        userService.checkUserDataCreate(userSrc);
    }

    @Test
    public void checkUserDataCreateFailInvalidEmail() {
        UserDto userOne = new UserDto("OwnerUser", "email");

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.checkUserDataCreate(userOne));
        assertEquals("Не верно указан email пользователя.", thrown.getMessage());
    }

    @Test
    public void checkUserDataCreateFailNullEmail() {
        UserDto userOne = new UserDto("OwnerUser", null);

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.checkUserDataCreate(userOne));
        assertEquals("Не верно указан email пользователя.", thrown.getMessage());
    }

    @Test
    public void checkUserDataCreateFailBlancName() {
        UserDto userOne = new UserDto("", "user@email.ru");

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.checkUserDataCreate(userOne));
        assertEquals("Не указано имя пользователя.", thrown.getMessage());
    }

    @Test
    public void checkUserDataCreateFailNullName() {
        UserDto userOne = new UserDto(null, "user@email.ru");

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.checkUserDataCreate(userOne));
        assertEquals("Не указано имя пользователя.", thrown.getMessage());
    }

    @Test
    void checkUserDataUpdateNormal() {
        UserDto userSrc = new UserDto("OwnerUser", "user@email.ru");
        userService.checkUserDataUpdate(userSrc);
    }
}