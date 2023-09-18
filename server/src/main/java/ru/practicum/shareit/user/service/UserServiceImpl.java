package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserInvalidDataException;
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
        User currentUser = UserMapper.toUser(userDto);
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
                initialUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                initialUser.setEmail(userDto.getEmail());
            }
            return userRepository.save(initialUser);
        }
        log.debug("Пользователь с id {} не найден.", userId);
        throw new UserInvalidDataException("Пользователь с id " + userId + " не найден.");
    }

    @Override
    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
