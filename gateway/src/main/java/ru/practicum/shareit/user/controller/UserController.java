package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable Long userId) {
        return userClient.findUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        return userClient.findAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        userService.checkUserDataCreate(userDto);
        ResponseEntity<Object> result = userClient.createUser(userDto);
        return result;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userService.checkUserDataUpdate(userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
        userClient.removeUser(userId);
    }
}
