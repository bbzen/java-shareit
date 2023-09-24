package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

@Service
public interface UserService {

    User createUser(UserDto userDto);

    User findUser(Long userId);

    List<User> findAllUsers();

    User updateUser(Long userId, UserDto userDto);

    void removeUser(Long userId);
}
