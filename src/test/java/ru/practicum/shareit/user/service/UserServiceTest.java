package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.UserInvalidDataException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private static UserRepository userRepository;
    @InjectMocks
    private static UserService userService;
    private User userOne;
    private User userTwo;

    @BeforeAll
    public static void initial() {
        userService = new UserServiceImpl(userRepository);
    }

    @BeforeEach
    public void setUp() {
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
    }

    @Test
    public void createUserNormal() {
        when(userRepository.save(Mockito.any()))
            .thenReturn(userOne);

        UserDto srcUserDto = UserMapper.toUserDto(userOne);
        User userResult = userService.createUser(srcUserDto);

        assertEquals(userOne.getId(), userResult.getId());
        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void createUserInvalidEmailFail() {
        userOne.setEmail("email");
        UserDto srcUserDto = UserMapper.toUserDto(userOne);

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.createUser(srcUserDto));
        assertEquals("Не верно указан email пользователя.", thrown.getMessage());
    }

    @Test
    public void createUserBlancNameFail() {
        userOne.setName("");
        UserDto srcUserDto = UserMapper.toUserDto(userOne);

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.createUser(srcUserDto));
        assertEquals("Не указано имя пользователя.", thrown.getMessage());
    }

    @Test
    public void createUserNullNameFail() {
        userOne.setName(null);
        UserDto srcUserDto = UserMapper.toUserDto(userOne);

        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.createUser(srcUserDto));
        assertEquals("Не указано имя пользователя.", thrown.getMessage());
    }

    @Test
    public void findUserNormal() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));

        User userResult = userService.findUser(userOne.getId());
        assertEquals(userOne.getId(), userResult.getId());
    }

    @Test
    public void findUserNoUserFail() {
        Exception thrown = assertThrows(UserNotFoundException.class, () -> userService.findUser(userOne.getId()));
        assertEquals("Пользователь " + userOne.getId() + " не найден", thrown.getMessage());
    }

    @Test
    public void findAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(userOne, userTwo));

        List<User> result = userService.findAllUsers();
        assertEquals(result.get(1).getId(), userTwo.getId());
    }

    @Test
    public void updateUserNormal() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));
        when(userRepository.save(Mockito.any()))
                .thenReturn(userOne);

        User userResult = userService.updateUser(userOne.getId(), UserMapper.toUserDto(userOne));

        assertEquals(userOne.getId(), userResult.getId());
        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void updateUserNullParams() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));
        when(userRepository.save(Mockito.any()))
                .thenReturn(userOne);

        UserDto srcDto = UserMapper.toUserDto(userOne);
        srcDto.setEmail(null);
        srcDto.setName(null);

        User userResult = userService.updateUser(userOne.getId(), srcDto);
        assertEquals(userOne.getName(), userResult.getName());
        assertEquals(userOne.getEmail(), userResult.getEmail());
    }

    @Test
    public void updateUserNoUserFail() {
        Exception thrown = assertThrows(UserInvalidDataException.class, () -> userService.updateUser(userOne.getId(), UserMapper.toUserDto(userOne)));
        assertEquals("Пользователь с id " + userOne.getId() + " не найден.", thrown.getMessage());
    }

    @Test
    public void removeUser() {
        userService.removeUser(userOne.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }
}