package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User mapUserDtoToUser(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail());
    }
}
