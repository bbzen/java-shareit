package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;

@Service
public interface UserService {

    void checkUserData(UserDto userDto);
}
