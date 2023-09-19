package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserInvalidDataException;
import ru.practicum.shareit.user.model.UserDto;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public void checkUserDataCreate(UserDto userDto) {
        checkUserEmail(userDto);
        checkUserName(userDto);
    }

    @Override
    public void checkUserDataUpdate(UserDto userDto) {
        if (userDto.getEmail() != null) {
            checkUserEmail(userDto);
        }
        if (userDto.getName() != null) {
            checkUserName(userDto);
        }
    }

    private void checkUserName(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.debug("Не указано имя пользователя.");
            throw new UserInvalidDataException("Не указано имя пользователя.");
        }
    }

    private void checkUserEmail(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().matches(".+@.+\\..+")) {
            log.debug("Не верно указан email пользователя.");
            throw new UserInvalidDataException("Не верно указан email пользователя.");
        }
    }
}
