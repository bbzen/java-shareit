package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User findUser(Long userId);

    User findUserByEmail(String email);

    List<User> findAllUsers();

    User updateUser(User user);

    void removeUser(Long userId);

    boolean containsUser(Long userId);

    boolean containsUserEmail(String email);
}
