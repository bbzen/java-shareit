package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserEmailException;
import ru.practicum.shareit.exception.model.UserExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(UserDto userDto) {
        checkUserData(userDto);
        checkUserInStorage(userDto);
        User currentUser = UserMapper.mapUserDtoToUser(userDto);
        return userStorage.createUser(currentUser);
    }

    public User findUser(Long userId) {
        return userStorage.findUser(userId);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User updateUser(Long userId, UserDto userDto) {
        if (userStorage.containsUser(userId)) {
            User initialUser = findUser(userId);
            if (userDto.getName() != null) {
                checkUserName(userDto);
                initialUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                checkUserEmail(userDto);
                checkEmailIsFree(userId, userDto);
                initialUser.setEmail(userDto.getEmail());
            }
            return userStorage.updateUser(initialUser);
        }
        log.debug("Пользователь с id {} не найден.", userId);
        throw new UserEmailException("Пользователь с id " + userId + " не найден.");
    }

    public void removeUser(Long userId) {
        userStorage.removeUser(userId);
    }

    private void checkUserData(UserDto userDto) {
        checkUserEmail(userDto);
        checkUserName(userDto);
    }

    private boolean checkUserName(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.debug("Не указано имя пользователя.");
            throw new UserEmailException("Не указано имя пользователя.");
        }
        return true;
    }

    private boolean checkUserEmail(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().matches(".+@.+\\..+")) {
            log.debug("Не верно указан email пользователя.");
            throw new UserEmailException("Не верно указан email пользователя.");
        }
        return true;
    }

    private boolean checkUserInStorage(UserDto userDto) {
        if (userStorage.containsUserEmail(userDto.getEmail())) {
            log.debug("Пользователь с email {} уже существует.", userDto.getEmail());
            throw new UserExistsException("Пользователь с email " + userDto.getEmail() + " уже существует.");
        }
        return false;
    }

    private boolean checkEmailIsFree(Long userId, UserDto userDto) {
        User currentUser = userStorage.findUserByEmail(userDto.getEmail());
        if (currentUser == null || userId.equals(currentUser.getId())) {
            return true;
        }
        log.debug("Пользователь с email {} уже существует.", userDto.getEmail());
        throw new UserExistsException("Пользователь с email " + userDto.getEmail() + " уже существует.");
    }
}
