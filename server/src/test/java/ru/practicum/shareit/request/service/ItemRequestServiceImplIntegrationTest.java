package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DirtiesContext
    public void createItemRequestNormal() {
        UserDto requesterUserDto = generator.nextObject(UserDto.class);
        requesterUserDto.setEmail("requester@mail.ru");
        User requesterUser = userService.createUser(requesterUserDto);
        ItemRequestInputDto itemRequestInputDto = generator.nextObject(ItemRequestInputDto.class);

        ItemRequestRespDto result = itemRequestService.createRequest(requesterUser.getId(), itemRequestInputDto);
        assertEquals(itemRequestInputDto.getDescription(), result.getDescription());
    }
}