package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> userMap;
    private Long id;

    public UserStorageInMemory() {
        this.userMap = new HashMap<>();
        this.id = 0L;
    }

    @Override
    public User createUser(User user) {
        user.setId(++id);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public User findUserByEmail(String email) {
        return userMap.values().stream()
                .filter(o -> o.getEmail().equalsIgnoreCase(email))
                .findAny().orElse(null);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User updateUser(User user) {
        return userMap.put(user.getId(), user);
    }

    @Override
    public void removeUser(Long userId) {
        userMap.remove(userId);
    }

    @Override
    public boolean containsUser(Long userId) {
        return userMap.containsKey(userId);
    }

    @Override
    public boolean containsUserEmail(String email) {
        return userMap.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(e -> e.equalsIgnoreCase(email.toLowerCase()));
    }
}
