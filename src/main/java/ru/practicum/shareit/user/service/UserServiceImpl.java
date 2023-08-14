package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserEmailException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) {
        checkUserData(userDto);
        User currentUser = UserMapper.mapUserDtoToUser(userDto);
        return userRepository.save(currentUser);
    }

    @Override
    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь " + userId + " не найден"));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        if (userRepository.findById(userId).isPresent()) {
            User initialUser = findUser(userId);
            if (userDto.getName() != null) {
                checkUserName(userDto);
                initialUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                checkUserEmail(userDto);
                initialUser.setEmail(userDto.getEmail());
            }
            return userRepository.save(initialUser);
        }
        log.debug("Пользователь с id {} не найден.", userId);
        throw new UserEmailException("Пользователь с id " + userId + " не найден.");
    }

    @Override
    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void checkUserData(UserDto  userDto) {
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
}
